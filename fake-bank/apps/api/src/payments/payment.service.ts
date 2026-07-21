import { randomUUID } from 'node:crypto';
import type { ApiConfig } from '../server/config.js';
import type { ScenarioEngineService } from '../scenarios/scenario.service.js';
import type { WebhookDeliveryService } from '../webhooks/webhook.delivery.js';
import { InvalidTestCardError, resolveTestCard } from './card-simulation.js';
import {
  createPaymentLifecycleService,
  InvalidPaymentTransitionError,
} from './payment.lifecycle.js';
import type { CreatePaymentRecordInput, PaymentRepository } from './payment.repository.js';
import type {
  ConfirmPaymentInput,
  CreatePaymentInput,
  CreatePaymentResponse,
  Payment,
} from './payment.types.js';

export class PaymentNotFoundError extends Error {
  constructor(readonly paymentId: string) {
    super(`Payment was not found: ${paymentId}`);
  }
}

export { InvalidPaymentTransitionError, InvalidTestCardError };

export type PaymentService = {
  createPayment(input: CreatePaymentInput): Promise<CreatePaymentResponse>;
  getPayment(paymentId: string): Promise<Payment | null>;
  confirmPayment(paymentId: string, input: ConfirmPaymentInput): Promise<Payment>;
};

export function createPaymentService(
  repository: PaymentRepository,
  config: Pick<ApiConfig, 'checkoutBaseUrl'>,
  webhooks?: WebhookDeliveryService,
  scenarios?: ScenarioEngineService,
): PaymentService {
  const lifecycle = createPaymentLifecycleService(repository);

  return {
    createPayment(input: CreatePaymentInput): Promise<CreatePaymentResponse> {
      const paymentId = createPaymentId();
      const checkoutUrl = `${config.checkoutBaseUrl}/checkout/${paymentId}`;
      const record: CreatePaymentRecordInput = {
        ...input,
        payment_id: paymentId,
        status: 'created',
        checkout_url: checkoutUrl,
      };

      return repository.createPayment(record).then((payment) => ({
        payment_id: payment.payment_id,
        status: payment.status,
        checkout_url: payment.checkout_url,
      }));
    },

    getPayment(paymentId: string): Promise<Payment | null> {
      return repository.findPaymentById(paymentId);
    },

    confirmPayment(paymentId: string, input: ConfirmPaymentInput): Promise<Payment> {
      return repository.findPaymentById(paymentId)
        .then((payment) => {
          if (!payment) {
            throw new PaymentNotFoundError(paymentId);
          }

          const result = resolveTestCard(input.card);
          const scenario = payment.scenario ?? (result === 'success' ? 'card_success' : 'card_declined');

          return ensurePending(payment).then((pendingPayment) => {
            const scenarioPayment = { ...pendingPayment, scenario };

            if (scenarios) {
              return scenarios.executeAfterConfirmation(scenarioPayment);
            }

            const terminalStatus = scenario === 'card_success' ? 'succeeded' : 'failed';
            const reason = scenario === 'card_success' ? 'test_card_success' : 'test_card_declined';
            return lifecycle
              .transitionPayment(scenarioPayment, terminalStatus, reason)
              .then((transition) => {
                if (!webhooks) {
                  return transition.payment;
                }

                return webhooks
                  .deliverPaymentLifecycleEvent(transition.payment, transition.lifecycleEvent)
                  .then(() => transition.payment);
              });
          });
        });
    },
  };

  function ensurePending(payment: Payment): Promise<Payment> {
    if (payment.status === 'pending') {
      return Promise.resolve(payment);
    }

    return lifecycle
      .transitionPayment(payment, 'pending', 'payment_confirmed')
      .then((transition) => transition.payment);
  }
}

function createPaymentId(): string {
  return `pay_${randomUUID().replaceAll('-', '').slice(0, 12)}`;
}
