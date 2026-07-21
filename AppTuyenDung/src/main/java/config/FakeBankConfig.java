package config;

/**
 * Configuration for the local Fake-bank simulator. None of these values are
 * credentials; the merchant id merely identifies this demo integration.
 */
public final class FakeBankConfig {
    public static final String CURRENCY = "VND";
    public static final String API_URL = env("FAKE_BANK_API_URL", "http://fake-bank-api:8080");
    public static final String WEBHOOK_URL = env(
            "FAKE_BANK_WEBHOOK_URL",
            "http://backend:8080/AppTuyenDung/api/payment/fake-bank/webhook");
    public static final String FE_BASE_URL = env("FE_BASE_URL", "http://localhost:3000");
    public static final String MERCHANT_ID = env("FAKE_BANK_MERCHANT_ID", "webtuyendung-local-demo");

    private FakeBankConfig() {
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
