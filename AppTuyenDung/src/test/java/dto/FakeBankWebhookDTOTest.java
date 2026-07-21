package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class FakeBankWebhookDTOTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void mapsFakeBankWebhookPayload() throws Exception {
        String payload = """
                {
                  "event": "payment.succeeded",
                  "event_id": "event_000001",
                  "payment_id": "pay_000001",
                  "merchant_id": "webtuyendung",
                  "merchant_reference": "PAY_123",
                  "amount": 199000,
                  "currency": "VND",
                  "created_at": "2026-07-20T10:00:00Z"
                }
                """;

        FakeBankWebhookDTO webhook = objectMapper.readValue(payload, FakeBankWebhookDTO.class);

        assertEquals("payment.succeeded", webhook.getEvent());
        assertEquals("event_000001", webhook.getEventId());
        assertEquals("pay_000001", webhook.getPaymentId());
        assertEquals("PAY_123", webhook.getMerchantReference());
        assertEquals(new BigDecimal("199000"), webhook.getAmount());
        assertEquals("VND", webhook.getCurrency());
    }
}
