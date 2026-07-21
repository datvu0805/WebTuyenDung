import type { QueryResultRow } from 'pg';
import type { DbPool } from '../server/db.js';
import type { WebhookDeliveryRepository } from './webhook.repository.js';
import type {
  CompleteWebhookDeliveryAttemptInput,
  CreateWebhookDeliveryAttemptInput,
  WebhookDeliveryAttempt,
  WebhookDeliveryStatus,
  WebhookEventName,
  WebhookPayload,
} from './webhook.types.js';

type WebhookDeliveryAttemptRow = QueryResultRow & {
  delivery_attempt_id: string;
  payment_id: string;
  event_name: WebhookEventName;
  target_url: string;
  payload: WebhookPayload;
  attempt_number: number;
  status: WebhookDeliveryStatus;
  response_status_code: number | null;
  response_summary: string | null;
  created_at: Date;
  updated_at: Date;
};

export function createPostgresWebhookDeliveryRepository(
  pool: DbPool,
): WebhookDeliveryRepository {
  return {
    createDeliveryAttempt(
      input: CreateWebhookDeliveryAttemptInput,
    ): Promise<WebhookDeliveryAttempt> {
      return pool
        .query<WebhookDeliveryAttemptRow>(
          `
            INSERT INTO webhook_delivery_attempts (
              delivery_attempt_id,
              payment_id,
              event_name,
              target_url,
              payload,
              attempt_number,
              status
            )
            VALUES ($1, $2, $3, $4, $5, $6, $7)
            RETURNING *
          `,
          [
            input.delivery_attempt_id,
            input.payment_id,
            input.event_name,
            input.target_url,
            input.payload,
            input.attempt_number,
            input.status,
          ],
        )
        .then((result) => toWebhookDeliveryAttempt(result.rows[0]));
    },

    completeDeliveryAttempt(
      input: CompleteWebhookDeliveryAttemptInput,
    ): Promise<WebhookDeliveryAttempt> {
      return pool
        .query<WebhookDeliveryAttemptRow>(
          `
            UPDATE webhook_delivery_attempts
            SET
              status = $2,
              response_status_code = $3,
              response_summary = $4,
              updated_at = now()
            WHERE delivery_attempt_id = $1
            RETURNING *
          `,
          [
            input.delivery_attempt_id,
            input.status,
            input.response_status_code ?? null,
            input.response_summary,
          ],
        )
        .then((result) => toWebhookDeliveryAttempt(result.rows[0]));
    },

    listDeliveryAttempts(paymentId: string): Promise<WebhookDeliveryAttempt[]> {
      return pool
        .query<WebhookDeliveryAttemptRow>(
          `
            SELECT *
            FROM webhook_delivery_attempts
            WHERE payment_id = $1
            ORDER BY created_at ASC
          `,
          [paymentId],
        )
        .then((result) => result.rows.map(toWebhookDeliveryAttempt));
    },
  };
}

function toWebhookDeliveryAttempt(
  row: WebhookDeliveryAttemptRow | undefined,
): WebhookDeliveryAttempt {
  if (!row) {
    throw new Error('Webhook delivery attempt row was not returned.');
  }

  const attempt: WebhookDeliveryAttempt = {
    delivery_attempt_id: row.delivery_attempt_id,
    payment_id: row.payment_id,
    event_name: row.event_name,
    target_url: row.target_url,
    payload: row.payload,
    attempt_number: row.attempt_number,
    status: row.status,
    created_at: row.created_at.toISOString(),
    updated_at: row.updated_at.toISOString(),
  };

  if (row.response_status_code !== null) {
    attempt.response_status_code = row.response_status_code;
  }

  if (row.response_summary !== null) {
    attempt.response_summary = row.response_summary;
  }

  return attempt;
}
