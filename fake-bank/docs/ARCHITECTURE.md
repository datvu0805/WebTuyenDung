# Architecture

## Project Positioning

Fake-bank is an independent open-source payment provider simulator. It is designed to behave like an external payment provider from the perspective of a merchant integration, while remaining safe, local, deterministic, and free from real financial processing.

The project must stay provider-neutral and integration-neutral. Its architecture describes Fake-bank as a standalone product, not as an adapter, plugin, or companion for any external system.

## Purpose

Fake-bank exists to give developers a realistic payment provider boundary without requiring access to a real acquiring service. It provides the behavior that payment integrations usually depend on:

- payment creation through an API
- hosted buyer checkout
- deterministic card outcomes
- payment lifecycle transitions
- webhook delivery to merchant systems
- scenario-based failure and timing behavior

The goal is not to mock an internal function call. The goal is to simulate the external provider experience: a merchant creates a payment, a buyer completes checkout, the provider updates payment state, and the merchant receives asynchronous events.

## Target Users

- Developers building payment integrations
- QA engineers testing payment flows
- Tooling authors who need a deterministic provider simulator
- Technical writers and educators demonstrating payment lifecycles

## Supported Use Cases

- Create a card payment through an API
- Redirect a buyer to a hosted checkout page
- Confirm payment with deterministic test cards
- Receive success or failure webhooks
- Exercise delayed, duplicate, timeout, and provider error scenarios
- Demonstrate a complete payment lifecycle in local or demo environments
- Test merchant-side idempotency, timeout handling, and asynchronous event handling

## Architecture Principles

- Contract-first: public API behavior is defined before implementation behavior.
- Deterministic by default: the same scenario should produce the same observable result.
- Explicit scenarios: payment outcomes must be selected through named scenarios, not hidden random behavior.
- External boundary realism: Fake-bank should look and feel like a separate payment provider to its consumers.
- Minimal product surface: the first architecture supports payment simulation, not a full financial back office.
- Documentation-first behavior: supported flows, scenarios, and limitations must be visible in project documentation.

## System Boundary

Fake-bank owns the payment provider simulator boundary. Inside that boundary it manages payment records, checkout state, scenario execution, and webhook delivery attempts.

Systems outside the boundary are treated as merchant systems or buyer browsers. Fake-bank does not own merchant business logic, buyer identity, inventory, fulfillment, tax, accounting, or settlement.

```text
Outside Fake-bank                         Inside Fake-bank

Merchant application  ----------------->  Payment API
Buyer browser         ----------------->  Hosted Checkout
Merchant webhook URL  <-----------------  Webhook Engine
```

## Component Model

Fake-bank is composed of five architectural components. The components describe responsibilities and boundaries only; they do not prescribe a runtime, framework, database, or deployment topology.

### API Service

The API service exposes the public payment provider contract. It is responsible for accepting payment creation requests, returning payment state, and accepting checkout confirmation requests.

The API service validates request shape against the public contract and returns stable response models. It should not hide scenario behavior inside ad hoc request handling; scenario decisions belong to the scenario engine.

### Checkout UI

The checkout UI provides the hosted buyer-facing payment page. It displays payment details, accepts test card input, submits confirmation to Fake-bank, and presents the resulting payment state to the buyer.

The checkout UI is part of the provider simulator, not an optional demo shell. It is required because hosted checkout is a core part of the external payment provider flow.

### Webhook Engine

The webhook engine delivers payment lifecycle events to merchant webhook URLs. It owns event payload construction, delivery attempts, delivery status, and duplicate delivery scenarios.

Webhook delivery is asynchronous from the merchant perspective. Merchant systems must be able to receive the same event more than once, receive events after a delay, or receive no event in timeout scenarios.

### Scenario Engine

The scenario engine controls payment outcomes and timing. It maps explicit scenario names to lifecycle transitions and webhook behavior.

Initial scenarios:

- `card_success`: payment succeeds and emits a success webhook
- `card_declined`: payment fails and emits a failure webhook
- `delayed_success`: payment remains pending before succeeding
- `timeout`: payment remains pending without a terminal result
- `duplicate_webhook`: payment succeeds and emits repeated success webhooks
- `provider_error`: provider-side error simulation

The scenario engine is the only component that should decide simulated provider outcomes.

### Persistence Layer

The persistence layer stores simulator state in PostgreSQL. This includes payments,
lifecycle events, scenario execution state, and webhook delivery attempts.

## External Provider Model

The expected external flow is:

1. A merchant creates a payment through the Fake-bank API.
2. Fake-bank returns a payment identifier and hosted checkout URL.
3. The buyer opens the hosted checkout page.
4. The buyer enters a supported test card.
5. Fake-bank confirms the payment and applies the selected scenario.
6. Fake-bank updates payment state.
7. Fake-bank delivers webhook events to the merchant webhook URL.
8. The merchant handles the webhook and updates its own state.

```text
Merchant
    |
    | create payment
    v
Fake-bank API
    |
    | checkout URL
    v
Buyer Browser
    |
    | hosted payment page
    v
Buyer Checkout
    |
    | confirmation
    v
Payment Processing
    |
    | lifecycle event
    v
Webhook Delivery
    |
    | webhook POST
    v
Merchant
```

## Payment Lifecycle

The minimal lifecycle is intentionally small:

```text
created
   |
   v
pending
   |
   +--> succeeded
   |
   +--> failed
```

`created` means the payment record exists. `pending` means the buyer-facing payment flow is receiving or processing confirmation. `succeeded` and `failed` are terminal states for the first version.

Additional scenarios may keep a payment pending, delay a terminal state, return a provider error, or repeat webhook delivery.

## Public Contracts

The public API contract is defined in `openapi/openapi.yaml`. Architecture decisions must be reflected in that contract before implementation behavior is added.

The public API contract includes:

- create payment
- retrieve payment
- confirm payment

Webhook payloads are also public contracts. They must remain stable, documented, and suitable for merchant-side integration tests.

## Non-goals

Fake-bank does not provide:

- Real banking
- Real acquiring
- Real card processing
- PCI compliance
- Production payment security controls
- Card storage
- Buyer authentication
- Merchant onboarding
- Settlement
- Refunds
- Payouts
- Disputes
- Chargebacks
- Reconciliation
- Ledger accounting
- Fraud scoring
- Real exchange-rate or multi-currency business logic
- A merchant dashboard
- Production observability guarantees
