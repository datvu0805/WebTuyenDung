export type WebhookEventName = 'payment.succeeded' | 'payment.failed';
export type WebhookDeliveryStatus = 'pending' | 'delivered' | 'failed';

export type WebhookPayload = {
  event: WebhookEventName;
  event_id: string;
  payment_id: string;
  merchant_id?: string;
  merchant_reference?: string;
  amount: number;
  currency: string;
  created_at: string;
};

export type WebhookDeliveryAttempt = {
  delivery_attempt_id: string;
  payment_id: string;
  event_name: WebhookEventName;
  target_url: string;
  payload: WebhookPayload;
  attempt_number: number;
  status: WebhookDeliveryStatus;
  response_status_code?: number;
  response_summary?: string;
  created_at: string;
  updated_at: string;
};

export type CreateWebhookDeliveryAttemptInput = {
  delivery_attempt_id: string;
  payment_id: string;
  event_name: WebhookEventName;
  target_url: string;
  payload: WebhookPayload;
  attempt_number: number;
  status: 'pending';
};

export type CompleteWebhookDeliveryAttemptInput = {
  delivery_attempt_id: string;
  status: Exclude<WebhookDeliveryStatus, 'pending'>;
  response_status_code?: number;
  response_summary: string;
};
