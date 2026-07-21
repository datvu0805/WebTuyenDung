import type { PaymentScenario, PaymentStatus } from '../payments/payment.types.js';

export type ScenarioTiming = 'immediate' | 'delayed' | 'none';

export type ScenarioWebhookBehavior = 'single' | 'duplicate' | 'none';

export type ScenarioDefinition = {
  scenario: PaymentScenario;
  name: string;
  description: string;
  timing: ScenarioTiming;
  terminal_status: Extract<PaymentStatus, 'succeeded' | 'failed'> | null;
  lifecycle_reason: string | null;
  webhook_behavior: ScenarioWebhookBehavior;
  delay_seconds?: number;
};

export type ScenarioExecutionState = {
  scenario_state_id: string;
  payment_id: string;
  scenario: PaymentScenario;
  current_step: string;
  scheduled_at?: string;
  completed_at?: string;
  created_at: string;
  updated_at: string;
};

export type CreateScenarioExecutionStateInput = {
  scenario_state_id: string;
  payment_id: string;
  scenario: PaymentScenario;
  current_step: string;
  scheduled_at?: string;
};

export type CompleteScenarioExecutionStateInput = {
  scenario_state_id: string;
  current_step: string;
};

export type ScenarioExecutionResult = {
  scenario: ScenarioDefinition;
  current_step: string;
  terminal_status: Extract<PaymentStatus, 'succeeded' | 'failed'> | null;
  lifecycle_reason: string | null;
  webhook_deliveries: number;
  scheduled_at?: string;
};
