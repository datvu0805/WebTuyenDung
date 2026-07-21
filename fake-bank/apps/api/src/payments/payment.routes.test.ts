import assert from 'node:assert/strict';
import { test } from 'node:test';
import type { FastifyInstance } from 'fastify';
import { createApiApp } from '../server/app.js';
import type { ApiConfig } from '../server/config.js';
import { createMemoryScenarioExecutionRepository } from '../scenarios/scenario.repository.memory.js';
import {
  createScenarioEngineService,
  type ScenarioTaskScheduler,
} from '../scenarios/scenario.service.js';
import { createWebhookDeliveryService } from '../webhooks/webhook.delivery.js';
import { createMemoryWebhookDeliveryRepository } from '../webhooks/webhook.repository.memory.js';
import { createPaymentLifecycleService } from './payment.lifecycle.js';
import { createMemoryPaymentRepository } from './payment.repository.memory.js';
import type { PaymentScenario } from './payment.types.js';

const config: ApiConfig = {
  host: 'localhost',
  port: 8080,
  publicBaseUrl: 'http://localhost:8080',
  checkoutBaseUrl: 'http://localhost:5173',
  databaseUrl: 'postgres://fake_bank:fake_bank@localhost:55432/fake_bank',
};

test('health endpoint returns ok', () => {
  const app = createApiApp({ config, payments: createMemoryPaymentRepository() });

  return app.inject({
    method: 'GET',
    url: '/health',
  }).then((response) => {
    assert.equal(response.statusCode, 200);
    assert.deepEqual(response.json(), { status: 'ok' });
    return app.close();
  });
});

test('creates a payment with created status and checkout URL', () => {
  const app = createApiApp({ config, payments: createMemoryPaymentRepository() });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      merchant_reference: 'order_10001',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
      scenario: 'card_success',
    },
  }).then((response) => {
    assert.equal(response.statusCode, 201);
    const body = response.json();
    assert.match(body.payment_id, /^pay_[A-Za-z0-9]+$/);
    assert.equal(body.status, 'created');
    assert.equal(body.checkout_url, `http://localhost:5173/checkout/${body.payment_id}`);
    return app.close();
  });
});

test('retrieves a created payment', () => {
  const app = createApiApp({ config, payments: createMemoryPaymentRepository() });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      merchant_reference: 'order_10001',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();

    return app.inject({
      method: 'GET',
      url: `/v1/payments/${created.payment_id}`,
    }).then((response) => {
      assert.equal(response.statusCode, 200);
      const payment = response.json();
      assert.equal(payment.payment_id, created.payment_id);
      assert.equal(payment.status, 'created');
      assert.equal(payment.merchant_id, 'demo-shop');
      assert.equal(payment.payment_method, 'card');
      return app.close();
    });
  });
});

test('returns not found for unknown payment', () => {
  const app = createApiApp({ config, payments: createMemoryPaymentRepository() });

  return app.inject({
    method: 'GET',
    url: '/v1/payments/pay_missing',
  }).then((response) => {
    assert.equal(response.statusCode, 404);
    assert.equal(response.json().error_code, 'payment_not_found');
    return app.close();
  });
});

test('rejects invalid create payment payload', () => {
  const app = createApiApp({ config, payments: createMemoryPaymentRepository() });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      amount: -1,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'not-a-url',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((response) => {
    assert.equal(response.statusCode, 400);
    return app.close();
  });
});

test('confirms a successful payment through created to pending to succeeded', () => {
  const payments = createMemoryPaymentRepository();
  const app = createApiApp({ config, payments });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();

    return app.inject({
      method: 'POST',
      url: `/v1/payments/${created.payment_id}/confirm`,
      payload: {
        payment_method: 'card',
        card: {
          number: '4242424242424242',
          expiry: '12/30',
          cvv: '123',
        },
      },
    }).then((response) => {
      assert.equal(response.statusCode, 200);
      const payment = response.json();
      assert.equal(payment.status, 'succeeded');

      return payments.listLifecycleEvents(created.payment_id).then((events) => {
        assert.deepEqual(events.map((event) => event.next_status), ['pending', 'succeeded']);
        assert.deepEqual(events.map((event) => event.reason), [
          'payment_confirmed',
          'test_card_success',
        ]);
        return app.close();
      });
    });
  });
});

test('delivers payment succeeded webhook after successful confirmation', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const deliveredPayloads: unknown[] = [];
  const app = createApiApp({
    config,
    payments,
    webhooks: createWebhookDeliveryService(webhookAttempts, (_url, payload) => {
      deliveredPayloads.push(payload);
      return Promise.resolve({ status: 204, body: '' });
    }),
  });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      merchant_reference: 'order_10001',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();

    return app.inject({
      method: 'POST',
      url: `/v1/payments/${created.payment_id}/confirm`,
      payload: {
        payment_method: 'card',
        card: {
          number: '4242424242424242',
          expiry: '12/30',
          cvv: '123',
        },
      },
    }).then((response) => {
      assert.equal(response.statusCode, 200);
      assert.equal(response.json().status, 'succeeded');
      assert.equal(deliveredPayloads.length, 1);

      return webhookAttempts.listDeliveryAttempts(created.payment_id).then((attempts) => {
        assert.equal(attempts.length, 1);
        assert.equal(attempts[0]?.event_name, 'payment.succeeded');
        assert.equal(attempts[0]?.status, 'delivered');
        assert.equal(attempts[0]?.response_status_code, 204);
        assert.equal(attempts[0]?.payload.event, 'payment.succeeded');
        assert.equal(attempts[0]?.payload.payment_id, created.payment_id);
        return app.close();
      });
    });
  });
});

test('confirms a declined payment through created to pending to failed', () => {
  const payments = createMemoryPaymentRepository();
  const app = createApiApp({ config, payments });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();

    return app.inject({
      method: 'POST',
      url: `/v1/payments/${created.payment_id}/confirm`,
      payload: {
        payment_method: 'card',
        card: {
          number: '4000000000000002',
          expiry: '12/30',
          cvv: '123',
        },
      },
    }).then((response) => {
      assert.equal(response.statusCode, 200);
      const payment = response.json();
      assert.equal(payment.status, 'failed');

      return payments.listLifecycleEvents(created.payment_id).then((events) => {
        assert.deepEqual(events.map((event) => event.next_status), ['pending', 'failed']);
        assert.deepEqual(events.map((event) => event.reason), [
          'payment_confirmed',
          'test_card_declined',
        ]);
        return app.close();
      });
    });
  });
});

test('delivers payment failed webhook after declined confirmation', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const app = createApiApp({
    config,
    payments,
    webhooks: createWebhookDeliveryService(webhookAttempts, () => {
      return Promise.resolve({ status: 200, body: 'ok' });
    }),
  });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();

    return app.inject({
      method: 'POST',
      url: `/v1/payments/${created.payment_id}/confirm`,
      payload: {
        payment_method: 'card',
        card: {
          number: '4000000000000002',
          expiry: '12/30',
          cvv: '123',
        },
      },
    }).then((response) => {
      assert.equal(response.statusCode, 200);
      assert.equal(response.json().status, 'failed');

      return webhookAttempts.listDeliveryAttempts(created.payment_id).then((attempts) => {
        assert.equal(attempts.length, 1);
        assert.equal(attempts[0]?.event_name, 'payment.failed');
        assert.equal(attempts[0]?.status, 'delivered');
        assert.equal(attempts[0]?.payload.event, 'payment.failed');
        return app.close();
      });
    });
  });
});

test('records failed webhook delivery without changing payment state', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const app = createApiApp({
    config,
    payments,
    webhooks: createWebhookDeliveryService(webhookAttempts, () => {
      return Promise.reject(new Error('Connection refused'));
    }),
  });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();

    return app.inject({
      method: 'POST',
      url: `/v1/payments/${created.payment_id}/confirm`,
      payload: {
        payment_method: 'card',
        card: {
          number: '4242424242424242',
          expiry: '12/30',
          cvv: '123',
        },
      },
    }).then((response) => {
      assert.equal(response.statusCode, 200);
      assert.equal(response.json().status, 'succeeded');

      return Promise.all([
        payments.findPaymentById(created.payment_id),
        webhookAttempts.listDeliveryAttempts(created.payment_id),
      ]).then(([payment, attempts]) => {
        assert.equal(payment?.status, 'succeeded');
        assert.equal(attempts.length, 1);
        assert.equal(attempts[0]?.status, 'failed');
        assert.equal(attempts[0]?.response_summary, 'Connection refused');
        return app.close();
      });
    });
  });
});

test('executes card_success scenario with success webhook', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const scenarios = createMemoryScenarioExecutionRepository();
  const webhooks = createWebhookDeliveryService(webhookAttempts, () => {
    return Promise.resolve({ status: 204, body: '' });
  });
  const app = createApiApp({
    config,
    payments,
    webhooks,
    scenarios: createScenarioEngineService(
      scenarios,
      payments,
      createPaymentLifecycleService(payments),
      webhooks,
    ),
  });

  return createScenarioPayment(app, 'card_success')
    .then((created) => confirmScenarioPayment(app, created.payment_id, '4242424242424242'))
    .then((response) => {
      assert.equal(response.statusCode, 200);
      assert.equal(response.json().status, 'succeeded');

      return webhookAttempts.listDeliveryAttempts(response.json().payment_id);
    })
    .then((attempts) => {
      assert.equal(attempts.length, 1);
      assert.equal(attempts[0]?.event_name, 'payment.succeeded');
      assert.equal(attempts[0]?.status, 'delivered');
      return app.close();
    });
});

test('executes card_declined scenario with failed webhook', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const scenarios = createMemoryScenarioExecutionRepository();
  const webhooks = createWebhookDeliveryService(webhookAttempts, () => {
    return Promise.resolve({ status: 200, body: 'ok' });
  });
  const app = createApiApp({
    config,
    payments,
    webhooks,
    scenarios: createScenarioEngineService(
      scenarios,
      payments,
      createPaymentLifecycleService(payments),
      webhooks,
    ),
  });

  return createScenarioPayment(app, 'card_declined')
    .then((created) => confirmScenarioPayment(app, created.payment_id, '4242424242424242'))
    .then((response) => {
      assert.equal(response.statusCode, 200);
      assert.equal(response.json().status, 'failed');

      return webhookAttempts.listDeliveryAttempts(response.json().payment_id);
    })
    .then((attempts) => {
      assert.equal(attempts.length, 1);
      assert.equal(attempts[0]?.event_name, 'payment.failed');
      assert.equal(attempts[0]?.status, 'delivered');
      return app.close();
    });
});

test('executes delayed_success scenario through scheduled continuation', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const scenarios = createMemoryScenarioExecutionRepository();
  const manualScheduler = createManualScenarioScheduler();
  const webhooks = createWebhookDeliveryService(webhookAttempts, () => {
    return Promise.resolve({ status: 200, body: 'ok' });
  });
  const app = createApiApp({
    config,
    payments,
    webhooks,
    scenarios: createScenarioEngineService(
      scenarios,
      payments,
      createPaymentLifecycleService(payments),
      webhooks,
      manualScheduler.scheduler,
    ),
  });

  return createScenarioPayment(app, 'delayed_success')
    .then((created) => {
      return confirmScenarioPayment(app, created.payment_id, '4242424242424242')
        .then((response) => {
          assert.equal(response.statusCode, 200);
          assert.equal(response.json().status, 'pending');
          assert.equal(manualScheduler.taskCount(), 1);
          return webhookAttempts.listDeliveryAttempts(created.payment_id)
            .then((attempts) => {
              assert.equal(attempts.length, 0);
              return manualScheduler.runNext();
            })
            .then(() => payments.findPaymentById(created.payment_id))
            .then((payment) => {
              assert.equal(payment?.status, 'succeeded');
              return webhookAttempts.listDeliveryAttempts(created.payment_id);
            });
        });
    })
    .then((attempts) => {
      assert.equal(attempts.length, 1);
      assert.equal(attempts[0]?.event_name, 'payment.succeeded');
      return app.close();
    });
});

test('executes timeout scenario without terminal transition', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const scenarios = createMemoryScenarioExecutionRepository();
  const manualScheduler = createManualScenarioScheduler();
  const webhooks = createWebhookDeliveryService(webhookAttempts, () => {
    return Promise.resolve({ status: 200, body: 'ok' });
  });
  const app = createApiApp({
    config,
    payments,
    webhooks,
    scenarios: createScenarioEngineService(
      scenarios,
      payments,
      createPaymentLifecycleService(payments),
      webhooks,
      manualScheduler.scheduler,
    ),
  });

  return createScenarioPayment(app, 'timeout')
    .then((created) => {
      return confirmScenarioPayment(app, created.payment_id, '4242424242424242')
        .then((response) => {
          assert.equal(response.statusCode, 200);
          assert.equal(response.json().status, 'pending');
          assert.equal(manualScheduler.taskCount(), 0);
          return Promise.all([
            scenarios.findScenarioStateByPaymentId(created.payment_id),
            webhookAttempts.listDeliveryAttempts(created.payment_id),
          ]);
        });
    })
    .then(([state, attempts]) => {
      assert.equal(state?.current_step, 'timeout');
      assert.equal(attempts.length, 0);
      return app.close();
    });
});

test('executes duplicate_webhook scenario with repeated payload delivery', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const scenarios = createMemoryScenarioExecutionRepository();
  const webhooks = createWebhookDeliveryService(webhookAttempts, () => {
    return Promise.resolve({ status: 200, body: 'ok' });
  });
  const app = createApiApp({
    config,
    payments,
    webhooks,
    scenarios: createScenarioEngineService(
      scenarios,
      payments,
      createPaymentLifecycleService(payments),
      webhooks,
    ),
  });

  return createScenarioPayment(app, 'duplicate_webhook')
    .then((created) => {
      return confirmScenarioPayment(app, created.payment_id, '4242424242424242')
        .then((response) => {
          assert.equal(response.statusCode, 200);
          assert.equal(response.json().status, 'succeeded');
          return webhookAttempts.listDeliveryAttempts(created.payment_id);
        });
    })
    .then((attempts) => {
      assert.equal(attempts.length, 3);
      assert.deepEqual(attempts.map((attempt) => attempt.attempt_number), [1, 2, 3]);
      assert.equal(attempts[0]?.payload.event_id, attempts[1]?.payload.event_id);
      assert.equal(attempts[1]?.payload.event_id, attempts[2]?.payload.event_id);
      assert.deepEqual(attempts.map((attempt) => attempt.status), [
        'delivered',
        'delivered',
        'delivered',
      ]);
      return app.close();
    });
});

test('executes provider_error scenario while preserving payment consistency', () => {
  const payments = createMemoryPaymentRepository();
  const webhookAttempts = createMemoryWebhookDeliveryRepository();
  const scenarios = createMemoryScenarioExecutionRepository();
  const webhooks = createWebhookDeliveryService(webhookAttempts, () => {
    return Promise.resolve({ status: 200, body: 'ok' });
  });
  const app = createApiApp({
    config,
    payments,
    webhooks,
    scenarios: createScenarioEngineService(
      scenarios,
      payments,
      createPaymentLifecycleService(payments),
      webhooks,
    ),
  });

  return createScenarioPayment(app, 'provider_error')
    .then((created) => {
      return confirmScenarioPayment(app, created.payment_id, '4242424242424242')
        .then((response) => {
          assert.equal(response.statusCode, 500);
          assert.equal(response.json().error_code, 'provider_error');
          return Promise.all([
            payments.findPaymentById(created.payment_id),
            scenarios.findScenarioStateByPaymentId(created.payment_id),
            webhookAttempts.listDeliveryAttempts(created.payment_id),
          ]);
        });
    })
    .then(([payment, state, attempts]) => {
      assert.equal(payment?.status, 'pending');
      assert.equal(state?.current_step, 'provider_error');
      assert.equal(attempts.length, 0);
      return app.close();
    });
});

test('rejects unsupported test card input', () => {
  const app = createApiApp({ config, payments: createMemoryPaymentRepository() });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();

    return app.inject({
      method: 'POST',
      url: `/v1/payments/${created.payment_id}/confirm`,
      payload: {
        payment_method: 'card',
        card: {
          number: '4111111111111111',
          expiry: '12/30',
          cvv: '123',
        },
      },
    }).then((response) => {
      assert.equal(response.statusCode, 400);
      assert.equal(response.json().error_code, 'invalid_request');
      return app.close();
    });
  });
});

test('rejects confirming a terminal payment', () => {
  const app = createApiApp({ config, payments: createMemoryPaymentRepository() });

  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
    },
  }).then((createdResponse) => {
    const created = createdResponse.json();
    const confirmPayload = {
      payment_method: 'card',
      card: {
        number: '4242424242424242',
        expiry: '12/30',
        cvv: '123',
      },
    };

    return app.inject({
      method: 'POST',
      url: `/v1/payments/${created.payment_id}/confirm`,
      payload: confirmPayload,
    }).then((firstConfirm) => {
      assert.equal(firstConfirm.statusCode, 200);
      return app.inject({
        method: 'POST',
        url: `/v1/payments/${created.payment_id}/confirm`,
        payload: confirmPayload,
      });
    }).then((secondConfirm) => {
      assert.equal(secondConfirm.statusCode, 409);
      assert.equal(secondConfirm.json().error_code, 'payment_already_finalized');
      return app.close();
    });
  });
});

test('rejects invalid lifecycle transitions', () => {
  const payments = createMemoryPaymentRepository();
  const lifecycle = createPaymentLifecycleService(payments);

  return payments.createPayment({
    payment_id: 'pay_invalidtransition',
    merchant_id: 'demo-shop',
    amount: 199.99,
    currency: 'EUR',
    payment_method: 'card',
    status: 'created',
    checkout_url: 'http://localhost:8080/checkout/pay_invalidtransition',
    return_url: 'https://merchant.example/checkout/return',
    webhook_url: 'https://merchant.example/webhooks/payments',
  }).then((createdPayment) => {
    return assert.rejects(
      () => lifecycle.transitionPayment(createdPayment, 'succeeded', 'invalid_direct_success'),
      /Invalid payment transition/,
    ).then(() => lifecycle.transitionPayment(createdPayment, 'pending', 'valid_pending'));
  }).then((pendingTransition) => {
    return assert.rejects(
      () => lifecycle.transitionPayment(pendingTransition.payment, 'created', 'invalid_reopen'),
      /Invalid payment transition/,
    ).then(() => lifecycle.transitionPayment(pendingTransition.payment, 'succeeded', 'valid_success'));
  }).then((succeededTransition) => {
    return assert.rejects(
      () => lifecycle.transitionPayment(succeededTransition.payment, 'failed', 'invalid_terminal_change'),
      /Invalid payment transition/,
    );
  });
});

function createScenarioPayment(app: FastifyInstance, scenario: PaymentScenario): Promise<{
  payment_id: string;
  status: string;
  checkout_url: string;
}> {
  return app.inject({
    method: 'POST',
    url: '/v1/payments',
    payload: {
      merchant_id: 'demo-shop',
      merchant_reference: 'order_10001',
      amount: 199.99,
      currency: 'EUR',
      payment_method: 'card',
      return_url: 'https://merchant.example/checkout/return',
      webhook_url: 'https://merchant.example/webhooks/payments',
      scenario,
    },
  }).then((response) => {
    assert.equal(response.statusCode, 201);
    return response.json();
  });
}

function confirmScenarioPayment(
  app: FastifyInstance,
  paymentId: string,
  cardNumber: string,
) {
  return app.inject({
    method: 'POST',
    url: `/v1/payments/${paymentId}/confirm`,
    payload: {
      payment_method: 'card',
      card: {
        number: cardNumber,
        expiry: '12/30',
        cvv: '123',
      },
    },
  });
}

function createManualScenarioScheduler(): {
  scheduler: ScenarioTaskScheduler;
  runNext(): Promise<void>;
  taskCount(): number;
} {
  const tasks: Array<() => Promise<void>> = [];

  return {
    scheduler: {
      schedule(task: () => Promise<void>): void {
        tasks.push(task);
      },
    },
    runNext(): Promise<void> {
      const task = tasks.shift();
      return task ? task() : Promise.resolve();
    },
    taskCount(): number {
      return tasks.length;
    },
  };
}
