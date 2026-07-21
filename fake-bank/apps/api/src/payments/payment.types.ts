export const paymentStatuses = ['created', 'pending', 'succeeded', 'failed'] as const;
export const paymentMethods = ['card'] as const;
export const paymentScenarios = [
  'card_success',
  'card_declined',
  'delayed_success',
  'timeout',
  'duplicate_webhook',
  'provider_error',
] as const;

export type PaymentStatus = (typeof paymentStatuses)[number];
export type PaymentMethod = (typeof paymentMethods)[number];
export type PaymentScenario = (typeof paymentScenarios)[number];

export type Payment = {
  payment_id: string;
  merchant_id: string;
  merchant_reference?: string;
  amount: number;
  currency: string;
  payment_method: PaymentMethod;
  status: PaymentStatus;
  checkout_url: string;
  return_url: string;
  webhook_url: string;
  scenario?: PaymentScenario;
  metadata?: Record<string, string>;
  created_at: string;
  updated_at: string;
};

export type CreatePaymentInput = {
  merchant_id: string;
  merchant_reference?: string;
  amount: number;
  currency: string;
  payment_method: PaymentMethod;
  return_url: string;
  webhook_url: string;
  scenario?: PaymentScenario;
  metadata?: Record<string, string>;
};

export type CreatePaymentResponse = {
  payment_id: string;
  status: PaymentStatus;
  checkout_url: string;
};

export type ConfirmPaymentInput = {
  payment_method: PaymentMethod;
  card: {
    number: string;
    expiry: string;
    cvv: string;
  };
};
