package service;

import config.FakeBankConfig;
import dto.FakeBankWebhookDTO;
import model.Transactions;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PaymentServiceWebhookValidationTest {

    @Test
    public void rejectsAmountMismatchBeforeVipActivation() {
        Transactions transaction = pendingTransaction();
        FakeBankWebhookDTO webhook = validWebhook();
        webhook.setAmount(new BigDecimal("199001"));

        assertFalse(PaymentService.matchesPaymentDetails(transaction, webhook));
    }

    @Test
    public void acceptsRepeatedWebhookForTheSameReconciledPayment() {
        Transactions transaction = pendingTransaction();
        FakeBankWebhookDTO webhook = validWebhook();

        assertTrue(PaymentService.matchesPaymentDetails(transaction, webhook));
        transaction.setStatus(1);
        assertTrue(PaymentService.matchesPaymentDetails(transaction, webhook));
    }

    @Test
    public void validatesRequiredFakeBankWebhookFields() {
        assertTrue(PaymentService.isValidWebhookPayload(validWebhook()));

        FakeBankWebhookDTO invalid = validWebhook();
        invalid.setCreatedAt("not-a-timestamp");
        assertFalse(PaymentService.isValidWebhookPayload(invalid));
    }

    private Transactions pendingTransaction() {
        Transactions transaction = new Transactions();
        transaction.setAmount(199000d);
        transaction.setProviderTransactionId("pay_000001");
        transaction.setStatus(0);
        return transaction;
    }

    private FakeBankWebhookDTO validWebhook() {
        FakeBankWebhookDTO webhook = new FakeBankWebhookDTO();
        webhook.setEvent("payment.succeeded");
        webhook.setEventId("event_000001");
        webhook.setPaymentId("pay_000001");
        webhook.setMerchantId(FakeBankConfig.MERCHANT_ID);
        webhook.setMerchantReference("PAY_123");
        webhook.setAmount(new BigDecimal("199000"));
        webhook.setCurrency("VND");
        webhook.setCreatedAt("2026-07-20T10:00:00Z");
        return webhook;
    }
}
