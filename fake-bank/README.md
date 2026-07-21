# Fake-bank

Open-source payment provider simulator with API, hosted checkout, webhook delivery,
deterministic scenarios, and a reference merchant integration.

## Purpose

Fake-bank provides a local payment provider experience for developers who need to test
payment integrations without using a real acquiring service. It exposes a payment API,
a hosted checkout page, card simulation, webhook delivery, and scenario-based payment
behavior.

## What Is Included

- Payment API defined by OpenAPI
- Hosted card checkout page
- Card test inputs for success and failure paths
- Payment lifecycle tracking
- Webhook delivery and delivery attempt tracking
- Deterministic payment scenarios
- PostgreSQL persistence
- Docker Compose local database
- Example merchant integration

## Quick Start

Start the complete Fake-bank product runtime in containers:

```sh
docker compose -f docker/docker-compose.yml up --build
```

This starts PostgreSQL, applies migrations, and then starts the API and Hosted Checkout UI.
The Example Merchant is not included. Open the API at `http://127.0.0.1:8080` and hosted
checkout pages at `http://127.0.0.1:5173`.

For native workspace development, install dependencies:

```sh
pnpm install
```

Start PostgreSQL:

```sh
pnpm dev:services
```

Start the API, checkout UI, and example merchant:

```sh
pnpm dev
```

Open the example merchant:

```text
http://127.0.0.1:8090
```

Create a payment, open the hosted checkout URL, and use a test card:

- Success: `4242424242424242`
- Failure: `4000000000000002`

Stop PostgreSQL:

```sh
pnpm dev:services:down
```

## Default Local URLs

- API: `http://localhost:8080`
- Hosted checkout UI: `http://localhost:5173`
- Example merchant: `http://127.0.0.1:8090`

## API Example

Create a payment:

```sh
curl -X POST http://localhost:8080/v1/payments \
  -H 'content-type: application/json' \
  -d '{
    "merchant_id": "demo-shop",
    "merchant_reference": "order_10001",
    "amount": 199.99,
    "currency": "EUR",
    "payment_method": "card",
    "return_url": "http://127.0.0.1:8090/",
    "webhook_url": "http://127.0.0.1:8090/webhooks/fake-bank",
    "scenario": "card_success"
  }'
```

The response includes a `checkout_url` that opens the hosted payment page.

## Scenarios

Fake-bank v0.1 supports:

- `card_success`
- `card_declined`
- `delayed_success`
- `timeout`
- `duplicate_webhook`
- `provider_error`

See [docs/SCENARIOS.md](docs/SCENARIOS.md).

## Repository Layout

```text
fake-bank/
├── apps/
│   ├── api/
│   └── checkout-ui/
├── db/
│   └── migrations/
├── docker/
│   └── docker-compose.yml
├── docs/
│   ├── ARCHITECTURE.md
│   ├── DEVELOPMENT.md
│   ├── DOMAIN.md
│   ├── INTEGRATION.md
│   ├── PERSISTENCE.md
│   └── SCENARIOS.md
├── examples/
│   └── merchant/
├── openapi/
│   └── openapi.yaml
├── packages/
│   ├── openapi/
│   └── shared/
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE
├── package.json
└── pnpm-workspace.yaml
```

## Validation

Run the full quality check:

```sh
pnpm validate
```

This validates the OpenAPI contract, database migrations, TypeScript projects, tests,
the example merchant smoke test, and the checkout build.

## Project Rules

Fake-bank is a standalone open-source project. Its code, package names, API contract,
documentation, examples, and Docker configuration describe Fake-bank only as an
independent payment provider simulator.

All project documentation is written in English.

## Non-goals

- Real banking
- Real acquiring
- Real card processing
- PCI compliance
- Authentication and merchant onboarding
- Refunds
- Payouts
- Settlement
- Multi-currency business logic

## License

Apache License 2.0. See [LICENSE](LICENSE).
