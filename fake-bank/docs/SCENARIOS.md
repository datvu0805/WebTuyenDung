# Payment Scenarios

Fake-bank scenarios define deterministic simulator behavior for provider-side payment tests.
They describe the simulated outcome, webhook behavior, and timing behavior for a payment.

Scenarios control simulated provider behavior only. They do not replace payment lifecycle
rules, persistence ownership, API response semantics, or merchant-side processing.

## Scenario Model

Each scenario has:

- `scenario`: stable scenario identity used in API requests and persisted payment records.
- `name`: human-readable scenario name.
- `description`: short explanation of the simulated provider behavior.
- `execution_behavior`: how the scenario is executed after buyer confirmation.
- `lifecycle_effect`: terminal or non-terminal payment state effect.
- `webhook_effect`: expected webhook delivery behavior.
- `timing_behavior`: immediate, delayed, or no terminal result.

Scenario outcomes are deterministic. Fake-bank does not use random behavior to decide
payment results.

## Scenario Catalog

### card_success

Name: Card success

Description: Simulates a successful card payment.

Execution behavior:

1. Payment is confirmed.
2. Payment moves to `pending`.
3. Payment moves to `succeeded`.

Lifecycle effect: `pending` to `succeeded`.

Webhook effect: one `payment.succeeded` webhook delivery.

Timing behavior: immediate terminal result.

### card_declined

Name: Card declined

Description: Simulates a declined card payment.

Execution behavior:

1. Payment is confirmed.
2. Payment moves to `pending`.
3. Payment moves to `failed`.

Lifecycle effect: `pending` to `failed`.

Webhook effect: one `payment.failed` webhook delivery.

Timing behavior: immediate terminal result.

### delayed_success

Name: Delayed success

Description: Simulates a payment that remains pending before succeeding.

Execution behavior:

1. Payment is confirmed.
2. Payment moves to `pending`.
3. Scenario state records the delayed terminal step.
4. Delayed execution moves the payment to `succeeded`.

Lifecycle effect: `pending` first, then `succeeded` through the lifecycle service.

Webhook effect: one `payment.succeeded` webhook delivery after the delayed terminal step.

Timing behavior: delayed terminal result. The default delay is 30 seconds.

### timeout

Name: Timeout

Description: Simulates a provider-side timeout where no terminal result is produced.

Execution behavior:

1. Payment is confirmed.
2. Payment moves to `pending`.
3. Scenario state records the timeout outcome.

Lifecycle effect: payment remains `pending`.

Webhook effect: no terminal payment webhook.

Timing behavior: no terminal result.

### duplicate_webhook

Name: Duplicate webhook

Description: Simulates a successful payment where the same webhook event is delivered more
than once.

Execution behavior:

1. Payment is confirmed.
2. Payment moves to `pending`.
3. Payment moves to `succeeded`.
4. The same terminal payment event is delivered multiple times.

Lifecycle effect: `pending` to `succeeded`.

Webhook effect: three delivery attempts for the same `payment.succeeded` event payload.

Timing behavior: immediate terminal result.

### provider_error

Name: Provider error

Description: Simulates a provider-side processing failure while preserving payment consistency.

Execution behavior:

1. Payment is confirmed.
2. Payment moves to `pending`.
3. Scenario state records the simulated provider failure.

Lifecycle effect: payment remains `pending`.

Webhook effect: no terminal payment webhook.

Timing behavior: no terminal result.

## Ownership Rules

- Payment lifecycle rules remain owned by the payment lifecycle service.
- Scenario execution state remains owned by the scenario engine.
- Webhook generation and delivery remain owned by the webhook engine.
- Merchant systems own webhook processing and merchant-side state.
