import type { PaymentScenario } from '../payments/payment.types.js';
import type { ScenarioDefinition } from './scenario.types.js';

const definitions: Record<PaymentScenario, ScenarioDefinition> = {
  card_success: {
    scenario: 'card_success',
    name: 'Card success',
    description: 'Successful card payment simulation.',
    timing: 'immediate',
    terminal_status: 'succeeded',
    lifecycle_reason: 'scenario_card_success',
    webhook_behavior: 'single',
  },
  card_declined: {
    scenario: 'card_declined',
    name: 'Card declined',
    description: 'Declined card payment simulation.',
    timing: 'immediate',
    terminal_status: 'failed',
    lifecycle_reason: 'scenario_card_declined',
    webhook_behavior: 'single',
  },
  delayed_success: {
    scenario: 'delayed_success',
    name: 'Delayed success',
    description: 'Delayed successful payment simulation.',
    timing: 'delayed',
    terminal_status: 'succeeded',
    lifecycle_reason: 'scenario_delayed_success',
    webhook_behavior: 'single',
    delay_seconds: 30,
  },
  timeout: {
    scenario: 'timeout',
    name: 'Timeout',
    description: 'Timeout simulation with no terminal payment result.',
    timing: 'none',
    terminal_status: null,
    lifecycle_reason: null,
    webhook_behavior: 'none',
  },
  duplicate_webhook: {
    scenario: 'duplicate_webhook',
    name: 'Duplicate webhook',
    description: 'Successful payment simulation with duplicate webhook delivery.',
    timing: 'immediate',
    terminal_status: 'succeeded',
    lifecycle_reason: 'scenario_duplicate_webhook',
    webhook_behavior: 'duplicate',
  },
  provider_error: {
    scenario: 'provider_error',
    name: 'Provider error',
    description: 'Provider error simulation that keeps payment state consistent.',
    timing: 'none',
    terminal_status: null,
    lifecycle_reason: null,
    webhook_behavior: 'none',
  },
};

export function getScenarioDefinition(scenario: PaymentScenario): ScenarioDefinition {
  return definitions[scenario];
}
