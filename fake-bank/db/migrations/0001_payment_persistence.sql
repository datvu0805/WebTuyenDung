BEGIN;

CREATE TYPE payment_status AS ENUM (
  'created',
  'pending',
  'succeeded',
  'failed'
);

CREATE TYPE payment_method AS ENUM (
  'card'
);

CREATE TYPE payment_scenario AS ENUM (
  'card_success',
  'card_declined',
  'delayed_success',
  'timeout',
  'duplicate_webhook',
  'provider_error'
);

CREATE TYPE webhook_delivery_status AS ENUM (
  'pending',
  'sent',
  'failed'
);

CREATE TABLE payments (
  payment_id text PRIMARY KEY,
  merchant_id text NOT NULL,
  merchant_reference text,
  amount numeric(18, 2) NOT NULL,
  currency char(3) NOT NULL,
  payment_method payment_method NOT NULL,
  status payment_status NOT NULL,
  checkout_url text NOT NULL,
  return_url text NOT NULL,
  webhook_url text NOT NULL,
  scenario payment_scenario,
  metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT payments_payment_id_format CHECK (payment_id ~ '^pay_[A-Za-z0-9]+$'),
  CONSTRAINT payments_amount_positive CHECK (amount > 0),
  CONSTRAINT payments_currency_format CHECK (currency ~ '^[A-Z]{3}$'),
  CONSTRAINT payments_metadata_object CHECK (jsonb_typeof(metadata) = 'object')
);

CREATE TABLE payment_lifecycle_events (
  lifecycle_event_id text PRIMARY KEY,
  payment_id text NOT NULL REFERENCES payments(payment_id) ON DELETE CASCADE,
  previous_status payment_status,
  next_status payment_status NOT NULL,
  reason text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT payment_lifecycle_events_id_format CHECK (
    lifecycle_event_id ~ '^lifecycle_[A-Za-z0-9]+$'
  ),
  CONSTRAINT payment_lifecycle_events_created_has_no_previous CHECK (
    next_status <> 'created' OR previous_status IS NULL
  ),
  CONSTRAINT payment_lifecycle_events_status_changed CHECK (
    previous_status IS NULL OR previous_status <> next_status
  )
);

CREATE TABLE scenario_execution_state (
  scenario_state_id text PRIMARY KEY,
  payment_id text NOT NULL UNIQUE REFERENCES payments(payment_id) ON DELETE CASCADE,
  scenario payment_scenario NOT NULL,
  current_step text NOT NULL,
  scheduled_at timestamptz,
  completed_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT scenario_execution_state_id_format CHECK (
    scenario_state_id ~ '^scenario_state_[A-Za-z0-9]+$'
  ),
  CONSTRAINT scenario_execution_state_completion_order CHECK (
    completed_at IS NULL OR completed_at >= created_at
  )
);

CREATE TABLE webhook_delivery_attempts (
  delivery_attempt_id text PRIMARY KEY,
  payment_id text NOT NULL REFERENCES payments(payment_id) ON DELETE CASCADE,
  event_name text NOT NULL,
  target_url text NOT NULL,
  payload jsonb NOT NULL,
  attempt_number integer NOT NULL,
  status webhook_delivery_status NOT NULL,
  response_status_code integer,
  response_summary text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT webhook_delivery_attempts_id_format CHECK (
    delivery_attempt_id ~ '^delivery_[A-Za-z0-9]+$'
  ),
  CONSTRAINT webhook_delivery_attempts_payload_object CHECK (
    jsonb_typeof(payload) = 'object'
  ),
  CONSTRAINT webhook_delivery_attempts_attempt_positive CHECK (attempt_number > 0),
  CONSTRAINT webhook_delivery_attempts_response_code_range CHECK (
    response_status_code IS NULL
    OR (response_status_code >= 100 AND response_status_code <= 599)
  )
);

CREATE INDEX payments_status_idx ON payments(status);
CREATE INDEX payments_merchant_id_idx ON payments(merchant_id);
CREATE INDEX payments_scenario_idx ON payments(scenario);
CREATE INDEX payment_lifecycle_events_payment_id_created_at_idx
  ON payment_lifecycle_events(payment_id, created_at);
CREATE INDEX scenario_execution_state_scheduled_at_idx
  ON scenario_execution_state(scheduled_at)
  WHERE completed_at IS NULL;
CREATE INDEX webhook_delivery_attempts_payment_id_created_at_idx
  ON webhook_delivery_attempts(payment_id, created_at);
CREATE INDEX webhook_delivery_attempts_status_created_at_idx
  ON webhook_delivery_attempts(status, created_at);

COMMIT;
