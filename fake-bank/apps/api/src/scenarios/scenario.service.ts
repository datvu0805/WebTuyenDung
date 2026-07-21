import { randomUUID } from 'node:crypto';
import type { PaymentLifecycleService } from '../payments/payment.lifecycle.js';
import type { PaymentRepository } from '../payments/payment.repository.js';
import type { Payment } from '../payments/payment.types.js';
import type { WebhookDeliveryService } from '../webhooks/webhook.delivery.js';
import { getScenarioDefinition } from './scenario.catalog.js';
import type { ScenarioExecutionRepository } from './scenario.repository.js';
import type {
  CreateScenarioExecutionStateInput,
  ScenarioExecutionResult,
} from './scenario.types.js';

export type ScenarioTaskScheduler = {
  schedule(task: () => Promise<void>, delayMs: number): void;
};

export type ScenarioEngineService = {
  executeAfterConfirmation(payment: Payment): Promise<Payment>;
  executeDelayedStep(paymentId: string): Promise<Payment | null>;
};

export class ScenarioProviderError extends Error {
  constructor(readonly paymentId: string) {
    super(`Provider error scenario was triggered for payment: ${paymentId}`);
  }
}

export function createScenarioEngineService(
  scenarios: ScenarioExecutionRepository,
  payments: PaymentRepository,
  lifecycle: PaymentLifecycleService,
  webhooks: WebhookDeliveryService,
  scheduler: ScenarioTaskScheduler = createTimeoutScheduler(),
): ScenarioEngineService {
  const service: ScenarioEngineService = {
    executeAfterConfirmation(payment: Payment): Promise<Payment> {
      const result = resolveScenarioExecution(payment);

      if (result.current_step !== 'terminal_ready') {
        return persistScenarioState(scenarios, payment, result)
          .then(() => {
            if (result.current_step === 'delayed_terminal_ready') {
              scheduler.schedule(
                () => service.executeDelayedStep(payment.payment_id).then(() => {}),
                getDelayMs(result),
              );
            }

            if (result.current_step === 'provider_error') {
              throw new ScenarioProviderError(payment.payment_id);
            }

            return payment;
          });
      }

      return applyTerminalResult(payment, result, lifecycle, webhooks);
    },

    executeDelayedStep(paymentId: string): Promise<Payment | null> {
      return scenarios.findScenarioStateByPaymentId(paymentId)
        .then((state) => {
          if (!state || state.current_step !== 'delayed_terminal_ready') {
            return null;
          }

          return payments.findPaymentById(paymentId)
            .then((payment) => {
              if (!payment || payment.status !== 'pending') {
                return null;
              }

              const result = resolveScenarioExecution(payment);

              if (!result.terminal_status || !result.lifecycle_reason) {
                return null;
              }

              return lifecycle
                .transitionPayment(payment, result.terminal_status, result.lifecycle_reason)
                .then((transition) => {
                  return webhooks
                    .deliverPaymentLifecycleEvent(transition.payment, transition.lifecycleEvent, {
                      deliveryCount: result.webhook_deliveries,
                    })
                    .then(() => scenarios.completeScenarioState({
                      scenario_state_id: state.scenario_state_id,
                      current_step: 'completed',
                    }))
                    .then(() => transition.payment);
                });
            });
        });
    },
  };

  return service;
}

function resolveScenarioExecution(payment: Payment): ScenarioExecutionResult {
  const definition = getScenarioDefinition(payment.scenario ?? 'card_success');

  if (definition.timing === 'immediate') {
    return {
      scenario: definition,
      current_step: 'terminal_ready',
      terminal_status: definition.terminal_status,
      lifecycle_reason: definition.lifecycle_reason,
      webhook_deliveries: definition.webhook_behavior === 'duplicate' ? 3 : 1,
    };
  }

  if (definition.timing === 'delayed') {
    return {
      scenario: definition,
      current_step: 'delayed_terminal_ready',
      terminal_status: definition.terminal_status,
      lifecycle_reason: definition.lifecycle_reason,
      webhook_deliveries: 1,
      scheduled_at: new Date(Date.now() + getDelayMs({ scenario: definition })).toISOString(),
    };
  }

  return {
    scenario: definition,
    current_step: definition.scenario === 'provider_error' ? 'provider_error' : 'timeout',
    terminal_status: null,
    lifecycle_reason: null,
    webhook_deliveries: 0,
  };
}

function persistScenarioState(
  scenarios: ScenarioExecutionRepository,
  payment: Payment,
  result: ScenarioExecutionResult,
): Promise<unknown> {
  const input: CreateScenarioExecutionStateInput = {
    scenario_state_id: createScenarioStateId(),
    payment_id: payment.payment_id,
    scenario: result.scenario.scenario,
    current_step: result.current_step,
  };

  if (result.scheduled_at) {
    input.scheduled_at = result.scheduled_at;
  }

  return scenarios.createScenarioState(input);
}

function applyTerminalResult(
  payment: Payment,
  result: ScenarioExecutionResult,
  lifecycle: PaymentLifecycleService,
  webhooks: WebhookDeliveryService,
): Promise<Payment> {
  if (!result.terminal_status || !result.lifecycle_reason) {
    return Promise.resolve(payment);
  }

  return lifecycle
    .transitionPayment(payment, result.terminal_status, result.lifecycle_reason)
    .then((transition) => {
      return webhooks
        .deliverPaymentLifecycleEvent(transition.payment, transition.lifecycleEvent, {
          deliveryCount: result.webhook_deliveries,
        })
        .then(() => transition.payment);
    });
}

function getDelayMs(result: Pick<ScenarioExecutionResult, 'scenario'>): number {
  return (result.scenario.delay_seconds ?? 0) * 1000;
}

function createScenarioStateId(): string {
  return `scenario_state_${randomUUID().replaceAll('-', '').slice(0, 12)}`;
}

function createTimeoutScheduler(): ScenarioTaskScheduler {
  return {
    schedule(task: () => Promise<void>, delayMs: number): void {
      setTimeout(() => {
        task().catch(() => undefined);
      }, delayMs);
    },
  };
}
