import { randomUUID } from 'node:crypto';
import type { PaymentRepository, PaymentTransitionResult } from './payment.repository.js';
import type { Payment, PaymentStatus } from './payment.types.js';

export class InvalidPaymentTransitionError extends Error {
  constructor(
    readonly paymentId: string,
    readonly previousStatus: PaymentStatus,
    readonly nextStatus: PaymentStatus,
  ) {
    super(`Invalid payment transition from ${previousStatus} to ${nextStatus}.`);
  }
}

export type PaymentLifecycleService = {
  transitionPayment(
    payment: Payment,
    nextStatus: PaymentStatus,
    reason: string,
  ): Promise<PaymentTransitionResult>;
};

const allowedTransitions: Record<PaymentStatus, readonly PaymentStatus[]> = {
  created: ['pending'],
  pending: ['succeeded', 'failed'],
  succeeded: [],
  failed: [],
};

export function createPaymentLifecycleService(
  repository: PaymentRepository,
): PaymentLifecycleService {
  return {
    transitionPayment(
      payment: Payment,
      nextStatus: PaymentStatus,
      reason: string,
    ): Promise<PaymentTransitionResult> {
      if (!canTransition(payment.status, nextStatus)) {
        return Promise.reject(
          new InvalidPaymentTransitionError(payment.payment_id, payment.status, nextStatus),
        );
      }

      return repository
        .transitionPayment({
          payment_id: payment.payment_id,
          previous_status: payment.status,
          next_status: nextStatus,
          reason,
          lifecycle_event_id: createLifecycleEventId(),
        })
        .then((updatedPayment) => {
          if (!updatedPayment) {
            throw new InvalidPaymentTransitionError(
              payment.payment_id,
              payment.status,
              nextStatus,
            );
          }

          return updatedPayment;
        });
    },
  };
}

export function canTransition(
  previousStatus: PaymentStatus,
  nextStatus: PaymentStatus,
): boolean {
  return allowedTransitions[previousStatus].includes(nextStatus);
}

function createLifecycleEventId(): string {
  return `lifecycle_${randomUUID().replaceAll('-', '').slice(0, 12)}`;
}
