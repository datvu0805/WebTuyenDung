# Reference Merchant Integration

This document describes the minimal merchant example in `examples/merchant`.
The example demonstrates Fake-bank as an external payment provider through the public API,
hosted checkout URL, and webhook delivery.

The example merchant is a demonstration client only. It is not part of the Fake-bank domain
model and does not add merchant business logic to Fake-bank.

## Integration Flow

```text
Example Merchant
    |
    | POST /v1/payments
    v
Fake-bank API
    |
    | checkout_url
    v
Buyer Checkout
    |
    | confirm test card payment
    v
Fake-bank Payment Lifecycle
    |
    | payment webhook
    v
Example Merchant
```

## Local Setup

Start the local infrastructure:

```sh
pnpm dev:services
```

Apply database migrations:

```sh
pnpm db:migrate
```

Start the Fake-bank API:

```sh
pnpm --filter @fake-bank/api dev
```

Start the hosted checkout UI:

```sh
pnpm --filter @fake-bank/checkout-ui dev
```

Start the example merchant:

```sh
pnpm --filter @fake-bank/example-merchant dev
```

Default URLs:

- Fake-bank API: `http://127.0.0.1:8080`
- Hosted checkout UI: `http://127.0.0.1:5173`
- Example merchant: `http://127.0.0.1:8090`
- Merchant webhook endpoint: `http://127.0.0.1:8090/webhooks/fake-bank`

## Merchant Setup

The example merchant uses these environment variables:

- `MERCHANT_HOST`: HTTP host. Default: `127.0.0.1`.
- `MERCHANT_PORT`: HTTP port. Default: `8090`.
- `MERCHANT_PUBLIC_BASE_URL`: public merchant URL used for webhook URLs. Default: `http://127.0.0.1:8090`.
- `FAKE_BANK_BASE_URL`: Fake-bank API base URL. Default: `http://127.0.0.1:8080`.

## Create Payment Request

When the merchant form is submitted, the example calls:

```http
POST /v1/payments
Content-Type: application/json
```

Example request:

```json
{
  "merchant_id": "example-merchant",
  "merchant_reference": "demo_order_123",
  "amount": 199.99,
  "currency": "EUR",
  "payment_method": "card",
  "return_url": "http://127.0.0.1:8090/",
  "webhook_url": "http://127.0.0.1:8090/webhooks/fake-bank",
  "scenario": "card_success"
}
```

Example response:

```json
{
  "payment_id": "pay_000001",
  "status": "created",
  "checkout_url": "http://127.0.0.1:5173/checkout/pay_000001"
}
```

The merchant stores the payment identifier, status, scenario, and checkout URL in local
in-memory state for demonstration purposes.

## Checkout Flow

1. Open the example merchant at `http://127.0.0.1:8090`.
2. Create a payment with `Successful payment` or `Failed payment`.
3. Open the hosted checkout link displayed by the merchant.
4. Confirm the card payment with a test card.

Test cards:

- Successful payment: `4242424242424242`
- Failed payment: `4000000000000002`

## Webhook Handling

Fake-bank sends payment lifecycle webhooks to the merchant webhook URL.

Example webhook payload:

```json
{
  "event": "payment.succeeded",
  "event_id": "event_000001",
  "payment_id": "pay_000001",
  "merchant_id": "example-merchant",
  "merchant_reference": "demo_order_123",
  "amount": 199.99,
  "currency": "EUR",
  "created_at": "2026-07-07T12:01:00Z"
}
```

The example merchant updates its local payment display when it receives:

- `payment.succeeded`
- `payment.failed`

## Expected Payment Lifecycle

Successful payment:

```text
created
    |
    v
pending
    |
    v
succeeded
```

Failed payment:

```text
created
    |
    v
pending
    |
    v
failed
```

## Validation

Run the example merchant smoke test:

```sh
pnpm --filter @fake-bank/example-merchant test
```

The smoke test starts the merchant against a local Fake-bank API stub, creates a payment,
checks that the checkout URL is displayed, sends a webhook payload, and verifies that the
merchant displays the final payment result.
