import type { FastifyReply } from 'fastify';

export type ApiErrorCode =
  | 'invalid_request'
  | 'payment_not_found'
  | 'payment_already_finalized'
  | 'provider_error';

export type ApiErrorResponse = {
  error_code: ApiErrorCode;
  message: string;
  request_id: string;
  details?: Record<string, unknown>;
};

export function sendError(
  reply: FastifyReply,
  statusCode: number,
  errorCode: ApiErrorCode,
  message: string,
  details?: Record<string, unknown>,
): FastifyReply {
  const body: ApiErrorResponse = {
    error_code: errorCode,
    message,
    request_id: reply.request.id,
  };

  if (details) {
    body.details = details;
  }

  return reply.code(statusCode).send(body);
}
