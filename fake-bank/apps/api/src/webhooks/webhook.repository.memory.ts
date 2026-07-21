import type {
  CompleteWebhookDeliveryAttemptInput,
  CreateWebhookDeliveryAttemptInput,
  WebhookDeliveryAttempt,
} from './webhook.types.js';
import type { WebhookDeliveryRepository } from './webhook.repository.js';

export function createMemoryWebhookDeliveryRepository(): WebhookDeliveryRepository {
  const attempts = new Map<string, WebhookDeliveryAttempt>();

  return {
    createDeliveryAttempt(
      input: CreateWebhookDeliveryAttemptInput,
    ): Promise<WebhookDeliveryAttempt> {
      const now = new Date().toISOString();
      const attempt: WebhookDeliveryAttempt = {
        ...input,
        created_at: now,
        updated_at: now,
      };

      attempts.set(attempt.delivery_attempt_id, attempt);
      return Promise.resolve(attempt);
    },

    completeDeliveryAttempt(
      input: CompleteWebhookDeliveryAttemptInput,
    ): Promise<WebhookDeliveryAttempt> {
      const attempt = attempts.get(input.delivery_attempt_id);

      if (!attempt) {
        throw new Error(`Delivery attempt was not found: ${input.delivery_attempt_id}`);
      }

      const updated: WebhookDeliveryAttempt = {
        ...attempt,
        status: input.status,
        response_summary: input.response_summary,
        updated_at: new Date().toISOString(),
      };

      if (input.response_status_code !== undefined) {
        updated.response_status_code = input.response_status_code;
      }

      attempts.set(updated.delivery_attempt_id, updated);
      return Promise.resolve(updated);
    },

    listDeliveryAttempts(paymentId: string): Promise<WebhookDeliveryAttempt[]> {
      return Promise.resolve(
        [...attempts.values()]
          .filter((attempt) => attempt.payment_id === paymentId)
          .sort((left, right) => left.created_at.localeCompare(right.created_at)),
      );
    },
  };
}
