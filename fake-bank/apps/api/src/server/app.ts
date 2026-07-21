import { randomUUID } from 'node:crypto';
import Fastify, { type FastifyInstance } from 'fastify';
import type { PaymentRepository } from '../payments/payment.repository.js';
import { registerPaymentRoutes } from '../payments/payment.routes.js';
import type { ScenarioEngineService } from '../scenarios/scenario.service.js';
import {
  createNoopWebhookDeliveryService,
  type WebhookDeliveryService,
} from '../webhooks/webhook.delivery.js';
import type { ApiConfig } from './config.js';
import { sendError } from './errors.js';

export type ApiAppDependencies = {
  config: ApiConfig;
  payments: PaymentRepository;
  webhooks?: WebhookDeliveryService;
  scenarios?: ScenarioEngineService;
};

export function createApiApp(dependencies: ApiAppDependencies): FastifyInstance {
  const app = Fastify({
    logger: true,
    genReqId: (request) => {
      const requestId = request.headers['x-request-id'];
      return typeof requestId === 'string' && requestId.length > 0
        ? requestId
        : `req_${randomUUID().replaceAll('-', '')}`;
    },
  });

  app.setErrorHandler((error: unknown, request, reply) => {
    if (isValidationError(error)) {
      sendError(reply, 400, 'invalid_request', 'Request validation failed.', {
        reason: error.message,
      });
      return;
    }

    request.log.error({ error }, 'Unhandled API error');
    sendError(reply, 500, 'provider_error', 'Provider simulator returned an error.');
  });

  app.get('/health', {
    schema: {
      response: {
        200: {
          type: 'object',
          additionalProperties: false,
          required: ['status'],
          properties: {
            status: { type: 'string', enum: ['ok'] },
          },
        },
      },
    },
  }, (_request, reply) => reply.send({ status: 'ok' }));

  registerPaymentRoutes(app, {
    ...dependencies,
    webhooks: dependencies.webhooks ?? createNoopWebhookDeliveryService(),
  });

  return app;
}

function isValidationError(error: unknown): error is { message: string; validation: unknown } {
  return (
    typeof error === 'object'
    && error !== null
    && 'validation' in error
    && 'message' in error
    && typeof error.message === 'string'
  );
}
