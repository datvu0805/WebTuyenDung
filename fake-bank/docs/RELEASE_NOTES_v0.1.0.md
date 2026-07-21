# Fake-bank v0.1.0 Release Notes

Fake-bank v0.1.0 is the initial public release of the payment provider simulator.

## Highlights

- Payment API
- Hosted Checkout
- Card simulation
- Webhook delivery
- Deterministic scenarios
- Example merchant integration

## Included Components

- Fastify API service
- React and Vite checkout UI
- PostgreSQL persistence
- Docker Compose local database
- OpenAPI contract
- Reference merchant example

## Supported Payment Flow

```text
Example Merchant
    |
    v
Create Payment API
    |
    v
Hosted Checkout
    |
    v
Payment Confirmation
    |
    v
Webhook Delivery
    |
    v
Merchant Result
```

## Supported Scenarios

- `card_success`
- `card_declined`
- `delayed_success`
- `timeout`
- `duplicate_webhook`
- `provider_error`

## Validation

The release quality gate includes:

- OpenAPI validation
- migration validation
- TypeScript type checks
- API tests
- checkout build
- example merchant smoke test
- repository reference scan
