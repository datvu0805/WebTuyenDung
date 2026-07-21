import type { QueryResultRow } from 'pg';
import type { DbPool } from '../server/db.js';
import type {
  CreatePaymentRecordInput,
  PaymentLifecycleEvent,
  PaymentRepository,
  PaymentTransitionInput,
  PaymentTransitionResult,
} from './payment.repository.js';
import type { Payment, PaymentScenario, PaymentStatus } from './payment.types.js';

type PaymentRow = QueryResultRow & {
  payment_id: string;
  merchant_id: string;
  merchant_reference: string | null;
  amount: string;
  currency: string;
  payment_method: Payment['payment_method'];
  status: PaymentStatus;
  checkout_url: string;
  return_url: string;
  webhook_url: string;
  scenario: PaymentScenario | null;
  metadata: Record<string, string>;
  created_at: Date;
  updated_at: Date;
};

type LifecycleEventRow = QueryResultRow & {
  lifecycle_event_id: string;
  payment_id: string;
  previous_status: PaymentStatus | null;
  next_status: PaymentStatus;
  reason: string;
  created_at: Date;
};

type PaymentTransitionRow = PaymentRow & {
  lifecycle_event_id: string;
  previous_status: PaymentStatus | null;
  next_status: PaymentStatus;
  reason: string;
  lifecycle_created_at: Date;
};

export function createPostgresPaymentRepository(pool: DbPool): PaymentRepository {
  return {
    createPayment(input: CreatePaymentRecordInput): Promise<Payment> {
      return pool
        .query<PaymentRow>(
          `
            INSERT INTO payments (
              payment_id,
              merchant_id,
              merchant_reference,
              amount,
              currency,
              payment_method,
              status,
              checkout_url,
              return_url,
              webhook_url,
              scenario,
              metadata
            )
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
            RETURNING *
          `,
          [
            input.payment_id,
            input.merchant_id,
            input.merchant_reference ?? null,
            input.amount,
            input.currency,
            input.payment_method,
            input.status,
            input.checkout_url,
            input.return_url,
            input.webhook_url,
            input.scenario ?? null,
            input.metadata ?? {},
          ],
        )
        .then((result) => toPayment(result.rows[0]));
    },

    findPaymentById(paymentId: string): Promise<Payment | null> {
      return pool
        .query<PaymentRow>('SELECT * FROM payments WHERE payment_id = $1', [paymentId])
        .then((result) => (result.rows[0] ? toPayment(result.rows[0]) : null));
    },

    transitionPayment(input: PaymentTransitionInput): Promise<PaymentTransitionResult | null> {
      return pool
        .query<PaymentTransitionRow>(
          `
            WITH updated_payment AS (
              UPDATE payments
              SET status = $3, updated_at = now()
              WHERE payment_id = $1 AND status = $2
              RETURNING *
            ),
            lifecycle_event AS (
              INSERT INTO payment_lifecycle_events (
                lifecycle_event_id,
                payment_id,
                previous_status,
                next_status,
                reason
              )
              SELECT $5, payment_id, $2, $3, $4
              FROM updated_payment
              RETURNING
                lifecycle_event_id,
                payment_id,
                previous_status,
                next_status,
                reason,
                created_at AS lifecycle_created_at
            )
            SELECT updated_payment.*, lifecycle_event.*
            FROM updated_payment
            JOIN lifecycle_event ON lifecycle_event.payment_id = updated_payment.payment_id
          `,
          [
            input.payment_id,
            input.previous_status,
            input.next_status,
            input.reason,
            input.lifecycle_event_id,
          ],
        )
        .then((result) => (result.rows[0] ? toPaymentTransitionResult(result.rows[0]) : null));
    },

    listLifecycleEvents(paymentId: string): Promise<PaymentLifecycleEvent[]> {
      return pool
        .query<LifecycleEventRow>(
          `
            SELECT *
            FROM payment_lifecycle_events
            WHERE payment_id = $1
            ORDER BY created_at ASC
          `,
          [paymentId],
        )
        .then((result) => result.rows.map(toLifecycleEvent));
    },
  };
}

function toPaymentTransitionResult(row: PaymentTransitionRow): PaymentTransitionResult {
  return {
    payment: toPayment(row),
    lifecycleEvent: toLifecycleEvent({
      lifecycle_event_id: row.lifecycle_event_id,
      payment_id: row.payment_id,
      previous_status: row.previous_status,
      next_status: row.next_status,
      reason: row.reason,
      created_at: row.lifecycle_created_at,
    }),
  };
}

function toLifecycleEvent(row: LifecycleEventRow): PaymentLifecycleEvent {
  const event: PaymentLifecycleEvent = {
    lifecycle_event_id: row.lifecycle_event_id,
    payment_id: row.payment_id,
    next_status: row.next_status,
    reason: row.reason,
    created_at: row.created_at.toISOString(),
  };

  if (row.previous_status !== null) {
    event.previous_status = row.previous_status;
  }

  return event;
}

function toPayment(row: PaymentRow | undefined): Payment {
  if (!row) {
    throw new Error('Payment row was not returned.');
  }

  const payment: Payment = {
    payment_id: row.payment_id,
    merchant_id: row.merchant_id,
    amount: Number(row.amount),
    currency: row.currency,
    payment_method: row.payment_method,
    status: row.status,
    checkout_url: row.checkout_url,
    return_url: row.return_url,
    webhook_url: row.webhook_url,
    metadata: row.metadata,
    created_at: row.created_at.toISOString(),
    updated_at: row.updated_at.toISOString(),
  };

  if (row.merchant_reference !== null) {
    payment.merchant_reference = row.merchant_reference;
  }

  if (row.scenario !== null) {
    payment.scenario = row.scenario;
  }

  return payment;
}
