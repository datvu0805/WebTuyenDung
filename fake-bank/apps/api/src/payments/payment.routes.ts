import type { FastifyInstance } from 'fastify';
import { ScenarioProviderError } from '../scenarios/scenario.service.js';
import type { ApiAppDependencies } from '../server/app.js';
import { sendError } from '../server/errors.js';
import {
  confirmPaymentBodySchema,
  createPaymentBodySchema,
  createPaymentResponseSchema,
  errorResponseSchema,
  paymentParamsSchema,
  paymentResponseSchema,
} from './payment.schemas.js';
import { createPaymentService } from './payment.service.js';
import {
  InvalidPaymentTransitionError,
  InvalidTestCardError,
  PaymentNotFoundError,
} from './payment.service.js';
import type { ConfirmPaymentInput, CreatePaymentInput } from './payment.types.js';

type PaymentParams = {
  payment_id: string;
};

export function registerPaymentRoutes(
  app: FastifyInstance,
  dependencies: ApiAppDependencies,
): void {
  const service = createPaymentService(
    dependencies.payments,
    dependencies.config,
    dependencies.webhooks,
    dependencies.scenarios,
  );

  app.post<{ Body: CreatePaymentInput }>('/v1/payments', {
    schema: {
      body: createPaymentBodySchema,
      response: {
        201: createPaymentResponseSchema,
        400: errorResponseSchema,
        500: errorResponseSchema,
      },
    },
  }, (request, reply) => {
    service.createPayment(request.body)
      .then((payment) => reply.code(201).send(payment))
      .catch((error: unknown) => {
        request.log.error({ error }, 'Failed to create payment');
        sendError(reply, 500, 'provider_error', 'Provider simulator returned an error.');
      });
  });

  app.get<{ Params: PaymentParams }>('/v1/payments/:payment_id', {
    schema: {
      params: paymentParamsSchema,
      response: {
        200: paymentResponseSchema,
        404: errorResponseSchema,
        500: errorResponseSchema,
      },
    },
  }, (request, reply) => {
    service.getPayment(request.params.payment_id)
      .then((payment) => {
        if (!payment) {
          sendError(reply, 404, 'payment_not_found', 'Payment was not found.');
          return;
        }

        reply.send(payment);
      })
      .catch((error: unknown) => {
        request.log.error({ error }, 'Failed to get payment');
        sendError(reply, 500, 'provider_error', 'Provider simulator returned an error.');
      });
  });

  app.post<{ Params: PaymentParams; Body: ConfirmPaymentInput }>(
    '/v1/payments/:payment_id/confirm',
    {
      schema: {
        params: paymentParamsSchema,
        body: confirmPaymentBodySchema,
        response: {
          200: paymentResponseSchema,
          400: errorResponseSchema,
          404: errorResponseSchema,
          409: errorResponseSchema,
          500: errorResponseSchema,
        },
      },
    },
    (request, reply) => {
      service.confirmPayment(request.params.payment_id, request.body)
        .then((payment) => reply.send(payment))
        .catch((error: unknown) => {
          if (error instanceof PaymentNotFoundError) {
            sendError(reply, 404, 'payment_not_found', 'Payment was not found.');
            return;
          }

          if (error instanceof InvalidTestCardError) {
            sendError(reply, 400, 'invalid_request', 'Unsupported test card.', {
              field: 'card.number',
            });
            return;
          }

          if (error instanceof InvalidPaymentTransitionError) {
            sendError(reply, 409, 'payment_already_finalized', 'Payment cannot be confirmed.', {
              previous_status: error.previousStatus,
              requested_status: error.nextStatus,
            });
            return;
          }

          if (error instanceof ScenarioProviderError) {
            sendError(reply, 500, 'provider_error', 'Provider simulator returned an error.');
            return;
          }

          request.log.error({ error }, 'Failed to confirm payment');
          sendError(reply, 500, 'provider_error', 'Provider simulator returned an error.');
        });
    },
  );
}
