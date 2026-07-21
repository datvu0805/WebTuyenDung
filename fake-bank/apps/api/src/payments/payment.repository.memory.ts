import type {
  CreatePaymentRecordInput,
  PaymentLifecycleEvent,
  PaymentRepository,
  PaymentTransitionInput,
  PaymentTransitionResult,
} from './payment.repository.js';
import type { Payment } from './payment.types.js';

export function createMemoryPaymentRepository(): PaymentRepository {
  const payments = new Map<string, Payment>();
  const events = new Map<string, PaymentLifecycleEvent[]>();

  return {
    createPayment(input: CreatePaymentRecordInput): Promise<Payment> {
      const now = new Date().toISOString();
      const payment: Payment = {
        ...input,
        created_at: now,
        updated_at: now,
      };

      payments.set(payment.payment_id, payment);
      events.set(payment.payment_id, []);
      return Promise.resolve(payment);
    },

    findPaymentById(paymentId: string): Promise<Payment | null> {
      return Promise.resolve(payments.get(paymentId) ?? null);
    },

    transitionPayment(input: PaymentTransitionInput): Promise<PaymentTransitionResult | null> {
      const payment = payments.get(input.payment_id);

      if (!payment || payment.status !== input.previous_status) {
        return Promise.resolve(null);
      }

      const now = new Date().toISOString();
      const updated: Payment = {
        ...payment,
        status: input.next_status,
        updated_at: now,
      };
      const paymentEvents = events.get(input.payment_id) ?? [];
      const lifecycleEvent: PaymentLifecycleEvent = {
        lifecycle_event_id: input.lifecycle_event_id,
        payment_id: input.payment_id,
        previous_status: input.previous_status,
        next_status: input.next_status,
        reason: input.reason,
        created_at: now,
      };

      payments.set(input.payment_id, updated);
      events.set(input.payment_id, [...paymentEvents, lifecycleEvent]);
      return Promise.resolve({
        payment: updated,
        lifecycleEvent,
      });
    },

    listLifecycleEvents(paymentId: string): Promise<PaymentLifecycleEvent[]> {
      return Promise.resolve([...(events.get(paymentId) ?? [])]);
    },
  };
}
