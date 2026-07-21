# Development

## Requirements

- Node.js 24 or later
- pnpm 11 or later
- Docker with Docker Compose

## Runtime Stack

Backend:

- Node.js 24 or later
- TypeScript
- Fastify

Frontend:

- React
- Vite
- TypeScript

Database:

- PostgreSQL
- Docker Compose for local services

Tooling:

- pnpm workspace
- TypeScript project references
- OpenAPI contract validation
- Redocly for API validation
- Conventional commits

## Local Setup

Install dependencies:

```sh
pnpm install
```

Start local infrastructure services:

```sh
pnpm dev:services
```

Start workspace development commands. This applies database migrations before starting
the API, checkout UI, and example merchant:

```sh
pnpm dev
```

Stop local infrastructure services:

```sh
pnpm dev:services:down
```

Optional local environment overrides can be placed in `.env`. The default local setup uses
`.env.example` values.

## Containerized Product Runtime

Start PostgreSQL, apply the existing database migrations, and start the Fake-bank API and
hosted checkout UI with:

```sh
docker compose -f docker/docker-compose.yml up --build
```

The product runtime is available at:

- API: `http://127.0.0.1:8080`
- Hosted checkout UI: `http://127.0.0.1:5173`
- PostgreSQL: `127.0.0.1:55432`

Compose waits for PostgreSQL readiness, runs migrations as a one-shot service, waits for
the API healthcheck, and then starts the checkout UI. The checkout UI proxies `/v1`
requests to the API over the internal Compose network.

The Example Merchant is not part of this product runtime. The native `pnpm dev:services`
command remains infrastructure-only and starts PostgreSQL for workspace development.

## Workspace Layout

```text
apps/
  api/
  checkout-ui/

examples/
  merchant/

packages/
  shared/
  openapi/
```

`apps/api` contains the backend application workspace.

`apps/checkout-ui` contains the hosted checkout application workspace.

`examples/merchant` contains the reference merchant integration example.

`packages/shared` contains shared TypeScript contracts and utilities for Fake-bank workspaces.

`packages/openapi` contains OpenAPI-related workspace tooling.

## API Service

Start the API service:

```sh
pnpm --filter @fake-bank/api dev
```

The API service listens on `http://localhost:8080` by default.

Create payments through the API and use the returned `checkout_url` to open the hosted checkout page.

## Checkout UI

Start the hosted checkout application:

```sh
pnpm --filter @fake-bank/checkout-ui dev
```

The checkout application listens on `http://localhost:5173` by default.

Hosted checkout URLs use this format:

```text
http://localhost:5173/checkout/{payment_id}
```

The checkout application loads payment state from the API and submits payment confirmation requests to the API. During local development, Vite proxies `/v1` requests to the API service.

Build the checkout application:

```sh
pnpm --filter @fake-bank/checkout-ui build
```

## Database

PostgreSQL runs through Docker Compose for local development.

Start the local database:

```sh
pnpm dev:services
```

Stop the local database:

```sh
pnpm dev:services:down
```

Database migrations live in `db/migrations`.

Apply migrations manually when needed:

```sh
pnpm db:migrate
```

The root `pnpm dev` command runs `pnpm db:migrate` before starting development servers.

## Example Merchant

Start the example merchant:

```sh
pnpm --filter @fake-bank/example-merchant dev
```

The example merchant listens on `http://127.0.0.1:8090` by default.

Run its smoke test:

```sh
pnpm --filter @fake-bank/example-merchant test
```

## API Contract

The API contract is defined in `openapi/openapi.yaml`. API implementation changes must be made contract-first.

Do not change implementation behavior before updating the OpenAPI contract when the public API shape or semantics change.

## Workspace Commands

Run all workspace development commands:

```sh
pnpm dev
```

Apply database migrations:

```sh
pnpm db:migrate
```

Run all type checks:

```sh
pnpm typecheck
```

Run all tests:

```sh
pnpm test
```

Run all validation commands:

```sh
pnpm validate
```

## Validation

Validate the OpenAPI contract with:

```sh
pnpm lint:openapi
```

Validate database migration structure with:

```sh
pnpm lint:migrations
```

Run tests with:

```sh
pnpm test
```

## Environment Variables

Local environment variables are defined in `.env.example`.

The default `pnpm dev:services` command uses `.env.example`. Copy `.env.example` to
`.env` only when local overrides are needed. The `.env` file is intentionally ignored by Git.

PostgreSQL variables:

- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `DATABASE_URL`

Application variables:

- `API_HOST`
- `API_PORT`
- `API_PUBLIC_BASE_URL`
- `CHECKOUT_UI_HOST`
- `CHECKOUT_UI_PORT`
- `CHECKOUT_PUBLIC_BASE_URL`
- `VITE_API_BASE_URL`

## Commit Rules

Use conventional commits for repository changes.

## Documentation Rules

All documentation in this repository must be written in English.
