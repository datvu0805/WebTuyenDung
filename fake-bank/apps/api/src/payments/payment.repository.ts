import type { CreatePaymentInput, Payment, PaymentStatus } from './payment.types.js';

export type CreatePaymentRecordInput = CreatePaymentInput & {
  payment_id: string;
  status: PaymentStatus;
  checkout_url: string;
};

export type PaymentLifecycleEvent = {
  lifecycle_event_id: string;
  payment_id: string;
  previous_status?: PaymentStatus;
  next_status: PaymentStatus;
  reason: string;
  created_at: string;
};

export type PaymentTransitionInput = {
  payment_id: string;
  previous_status: PaymentStatus;
  next_status: PaymentStatus;
  reason: string;
  lifecycle_event_id: string;
};

export type PaymentTransitionResult = {
  payment: Payment;
  lifecycleEvent: PaymentLifecycleEvent;
};

export type PaymentRepository = {
  createPayment(input: CreatePaymentRecordInput): Promise<Payment>;
  findPaymentById(paymentId: string): Promise<Payment | null>;
  transitionPayment(input: PaymentTransitionInput): Promise<PaymentTransitionResult | null>;
  listLifecycleEvents(paymentId: string): Promise<PaymentLifecycleEvent[]>;
};
