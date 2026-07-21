import { createPostgresPaymentRepository } from './payments/payment.repository.postgres.js';
import { createPaymentLifecycleService } from './payments/payment.lifecycle.js';
import { createPostgresScenarioExecutionRepository } from './scenarios/scenario.repository.postgres.js';
import { createScenarioEngineService } from './scenarios/scenario.service.js';
import { createApiApp } from './server/app.js';
import { loadConfig } from './server/config.js';
import { createPgPool } from './server/db.js';
import { createWebhookDeliveryService } from './webhooks/webhook.delivery.js';
import { createPostgresWebhookDeliveryRepository } from './webhooks/webhook.repository.postgres.js';

const config = loadConfig();
const pool = createPgPool(config.databaseUrl);
const payments = createPostgresPaymentRepository(pool);
const webhooks = createWebhookDeliveryService(createPostgresWebhookDeliveryRepository(pool));
const scenarios = createScenarioEngineService(
  createPostgresScenarioExecutionRepository(pool),
  payments,
  createPaymentLifecycleService(payments),
  webhooks,
);
const app = createApiApp({ config, payments, webhooks, scenarios });

app.listen({ host: config.host, port: config.port })
  .then((address) => {
    app.log.info({ address }, 'API service started');
  })
  .catch((error: unknown) => {
    app.log.error({ error }, 'API service failed to start');
    process.exit(1);
  });

const close = () => {
  app.close()
    .then(() => pool.end())
    .then(() => {
      process.exit(0);
    })
    .catch((error: unknown) => {
      app.log.error({ error }, 'API service failed to stop cleanly');
      process.exit(1);
    });
};

process.on('SIGINT', close);
process.on('SIGTERM', close);
