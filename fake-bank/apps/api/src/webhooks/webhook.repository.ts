import type {
  CompleteWebhookDeliveryAttemptInput,
  CreateWebhookDeliveryAttemptInput,
  WebhookDeliveryAttempt,
} from './webhook.types.js';

export type WebhookDeliveryRepository = {
  createDeliveryAttempt(
    input: CreateWebhookDeliveryAttemptInput,
  ): Promise<WebhookDeliveryAttempt>;
  completeDeliveryAttempt(
    input: CompleteWebhookDeliveryAttemptInput,
  ): Promise<WebhookDeliveryAttempt>;
  listDeliveryAttempts(paymentId: string): Promise<WebhookDeliveryAttempt[]>;
};
