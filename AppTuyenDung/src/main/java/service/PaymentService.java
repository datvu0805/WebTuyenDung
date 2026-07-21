package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.DatabaseConfig;
import config.FakeBankConfig;
import dao.ServicePackagesDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import dao.UserServicesMDDAO;
import dto.FakeBankWebhookDTO;
import dto.PaymentCreateResponseDTO;
import dto.ServicePackageDTO;
import dto.UserVipStatusDTO;
import exception.BusinessException;
import model.ServicePackages;
import model.Transactions;
import model.UserServicesMD;
import model.Users;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PaymentService {
    private static final Logger LOGGER = Logger.getLogger(PaymentService.class.getName());
    private static final String FAKE_BANK_PROVIDER = "FAKE_BANK";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final UserDAO userDAO = new UserDAO();
    private final ServicePackagesDAO packagesDAO = new ServicePackagesDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UserServicesMDDAO userServicesMDDAO = new UserServicesMDDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    public List<ServicePackageDTO> listPackages(String role) {
        List<ServicePackages> packages;
        if (role != null && !role.isBlank() && !"ADMIN".equalsIgnoreCase(role)) {
            packages = packagesDAO.getByAudience(role);
        } else {
            packages = packagesDAO.getAll();
        }
        List<ServicePackageDTO> result = new ArrayList<>();
        for (ServicePackages pkg : packages) {
            result.add(toDto(pkg));
        }
        return result;
    }

    public UserVipStatusDTO getVipStatus(int userId) {
        UserServicesMD active = userServicesMDDAO.findActiveByUserId(userId);
        UserVipStatusDTO dto = new UserVipStatusDTO();
        if (active == null) {
            dto.setActive(false);
            return dto;
        }
        dto.setActive(true);
        ServicePackages pkg = active.getPackageID();
        if (pkg != null) {
            dto.setPackageName(pkg.getPackageName());
            dto.setBenefitType(pkg.getBenifitType());
            dto.setTargetAudience(pkg.getTargetAudience());
        }
        if (active.getStartDate() != null) {
            dto.setStartDate(active.getStartDate().toString());
        }
        if (active.getEndDate() != null) {
            dto.setEndDate(active.getEndDate().toString());
        }
        return dto;
    }

    public PaymentCreateResponseDTO createPayment(int userId, String userRole, int packageId) {
        if (!"CANDIDATE".equalsIgnoreCase(userRole) && !"EMPLOYER".equalsIgnoreCase(userRole)) {
            throw new BusinessException.ValidationException("Chỉ ứng viên hoặc nhà tuyển dụng mới mua được gói VIP");
        }

        Users user = userDAO.getByID(userId);
        if (user == null) {
            throw new BusinessException.ValidationException("Không tìm thấy tài khoản người dùng");
        }

        ServicePackages pkg = packagesDAO.getPackageById(packageId);
        if (pkg == null) {
            throw new BusinessException.ValidationException("Gói dịch vụ không tồn tại");
        }

        String target = pkg.getTargetAudience() == null ? "" : pkg.getTargetAudience().trim();
        boolean audienceOk = target.equalsIgnoreCase(userRole)
                || ("CANDIDATE".equalsIgnoreCase(userRole) && ("2".equals(target) || "CANDIDATE".equalsIgnoreCase(target)))
                || ("EMPLOYER".equalsIgnoreCase(userRole) && ("3".equals(target) || "EMPLOYER".equalsIgnoreCase(target)));
        if (!audienceOk) {
            throw new BusinessException.ValidationException(
                    "Gói \"" + pkg.getPackageName() + "\" không dành cho vai trò " + userRole);
        }
        if (pkg.getPrice() == null || pkg.getPrice() <= 0) {
            throw new BusinessException.ValidationException("Giá gói không hợp lệ");
        }

        String txnRef = "PAY_" + UUID.randomUUID().toString().replace("-", "");
        long amount = Math.round(pkg.getPrice());
        Transactions trans = new Transactions();
        trans.setUserID(userId);
        trans.setTransactionType("FAKE_BANK_VIP");
        trans.setPaymentProvider(FAKE_BANK_PROVIDER);
        trans.setAmount(pkg.getPrice());
        trans.setStatus(0);
        trans.setPaymentStatus("PENDING");
        trans.setContent("Chờ thanh toán demo gói " + pkg.getPackageName());
        trans.setPackageId(packageId);
        trans.setTxnRef(txnRef);

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                transactionDAO.insertTransaction(conn, trans);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Không tạo được đơn thanh toán demo: " + e.getMessage(), e);
        }

        try {
            JsonNode payment = createFakeBankPayment(txnRef, pkg, userId);
            String paymentId = payment.path("payment_id").asText("");
            String checkoutUrl = payment.path("checkout_url").asText("");
            if (paymentId.isBlank() || checkoutUrl.isBlank()) {
                throw new IllegalStateException("Fake-bank không trả đủ payment_id hoặc checkout_url");
            }
            saveProviderTransactionId(trans.getId(), paymentId);
            return new PaymentCreateResponseDTO(checkoutUrl, txnRef, amount, packageId, pkg.getPackageName());
        } catch (Exception e) {
            markPaymentCreationFailed(trans.getId(), e.getMessage());
            LOGGER.warning("Không tạo được Fake-bank payment cho txnRef=" + txnRef + ": " + e.getMessage());
            throw new RuntimeException("Không kết nối được cổng thanh toán demo", e);
        }
    }

    private JsonNode getFakeBankPayment(String paymentId) throws Exception {
        if (!Pattern.matches("^pay_[A-Za-z0-9]+$", paymentId)) {
            throw new IllegalArgumentException("Mã payment không hợp lệ");
        }
        String url = FakeBankConfig.API_URL + "/v1/payments/" + paymentId;
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                throw new IllegalStateException("Fake-bank trả HTTP " + response.code());
            }
            JsonNode payment = objectMapper.readTree(body);
            if (!payment.isObject() || !payment.hasNonNull("payment_id")) {
                throw new IllegalStateException("Fake-bank trả dữ liệu không hợp lệ");
            }
            return payment;
        }
    }

    private boolean matchesProviderPayment(JsonNode payment, FakeBankWebhookDTO webhook, String expectedStatus) {
        boolean detailsMatch = webhook.getPaymentId().equals(payment.path("payment_id").asText())
                && FakeBankConfig.MERCHANT_ID.equals(payment.path("merchant_id").asText())
                && webhook.getMerchantReference().equals(payment.path("merchant_reference").asText())
                && webhook.getCurrency().equals(payment.path("currency").asText())
                && webhook.getAmount().compareTo(payment.path("amount").decimalValue()) == 0;
        String providerStatus = payment.path("status").asText();
        return detailsMatch
                && (("SUCCESS".equals(expectedStatus) && "succeeded".equals(providerStatus))
                || ("FAILED".equals(expectedStatus) && "failed".equals(providerStatus)));
    }

    private JsonNode createFakeBankPayment(String txnRef, ServicePackages pkg, int userId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("merchant_id", FakeBankConfig.MERCHANT_ID);
        body.put("merchant_reference", txnRef);
        body.put("amount", pkg.getPrice());
        body.put("currency", FakeBankConfig.CURRENCY);
        body.put("payment_method", "card");
        body.put("return_url", FakeBankConfig.FE_BASE_URL + "/payment/result?txnRef="
                + URLEncoder.encode(txnRef, StandardCharsets.UTF_8));
        // The callback must be reachable from the fake-bank container, not from the browser.
        body.put("webhook_url", FakeBankConfig.WEBHOOK_URL);
        body.put("metadata", Map.of(
                "package_id", String.valueOf(pkg.getId()),
                "user_id", String.valueOf(userId)));

        Request request = new Request.Builder()
                .url(FakeBankConfig.API_URL + "/v1/payments")
                .post(RequestBody.create(objectMapper.writeValueAsString(body), JSON))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                throw new IllegalStateException("Fake-bank trả HTTP " + response.code());
            }
            return objectMapper.readTree(responseBody);
        }
    }

    private void saveProviderTransactionId(int transactionId, String providerTransactionId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            if (!transactionDAO.setProviderTransactionId(conn, transactionId, providerTransactionId)) {
                conn.rollback();
                throw new IllegalStateException("Giao dịch không còn ở trạng thái chờ thanh toán");
            }
            conn.commit();
        }
    }

    private void markPaymentCreationFailed(int transactionId, String reason) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            if (!transactionDAO.markFailed(conn, transactionId,
                    "Không tạo được payment provider: " + reason, "FAILED")) {
                conn.rollback();
                return;
            }
            conn.commit();
        } catch (Exception e) {
            LOGGER.warning("Không thể đóng giao dịch tạo payment thất bại: " + e.getMessage());
        }
    }

    public Map<String, Object> handleFakeBankWebhook(FakeBankWebhookDTO webhook) {
        Map<String, Object> result = new HashMap<>();
        if (!isValidWebhook(webhook)) {
            return failure(result, "97", "Dữ liệu webhook không hợp lệ");
        }

        String status = "payment.succeeded".equals(webhook.getEvent()) ? SUCCESS : FAILED;
        JsonNode providerPayment;
        try {
            providerPayment = getFakeBankPayment(webhook.getPaymentId());
        } catch (Exception e) {
            LOGGER.warning("Không xác minh được payment Fake-bank " + webhook.getPaymentId() + ": " + e.getMessage());
            return failure(result, "96", "Không xác minh được giao dịch với cổng demo");
        }
        if (!matchesProviderPayment(providerPayment, webhook, status)) {
            return failure(result, "04", "Fake-bank trả về dữ liệu giao dịch không hợp lệ");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            Transactions trans = transactionDAO.findByTxnRef(conn, webhook.getMerchantReference());
            if (trans == null) {
                return rollbackFailure(conn, result, "01", "Order not found");
            }
            if (!FAKE_BANK_PROVIDER.equals(trans.getPaymentProvider())) {
                return rollbackFailure(conn, result, "01", "Unsupported payment provider");
            }
            if (!matchesPaymentDetails(trans, webhook)) {
                return rollbackFailure(conn, result, "04", "Invalid payment details");
            }

            if (trans.getStatus() == 1) {
                if (SUCCESS.equals(status)) {
                    conn.rollback();
                    result.put("RspCode", "00");
                    result.put("Message", "Already processed");
                    result.put("success", true);
                    result.put("alreadyProcessed", true);
                    return result;
                }
                conn.rollback();
                result.put("RspCode", "00");
                result.put("Message", "Already processed");
                result.put("success", false);
                result.put("alreadyProcessed", true);
                return result;
            }
            if (trans.getStatus() == 2) {
                return rollbackFailure(conn, result, "02", "Order already closed");
            }

            if (SUCCESS.equals(status)) {
                if (!transactionDAO.markSuccess(conn, trans.getId(), webhook.getPaymentId())) {
                    return rollbackFailure(conn, result, "02", "Order already processed");
                }
                activateVip(conn, trans.getUserID(), trans.getPackageId());
                conn.commit();
                result.put("RspCode", "00");
                result.put("Message", "Payment confirmed");
                result.put("success", true);
                result.put("txnRef", webhook.getMerchantReference());
                return result;
            }

            boolean marked = transactionDAO.markFailed(conn, trans.getId(),
                    "Thanh toán Fake-bank bị từ chối", FAILED);
            if (!marked) {
                return rollbackFailure(conn, result, "02", "Order already closed");
            }
            conn.commit();
            result.put("RspCode", "00");
            result.put("Message", "Payment result recorded");
            result.put("success", false);
            result.put("txnRef", webhook.getMerchantReference());
            result.put("status", FAILED);
            return result;
        } catch (Exception e) {
            rollback(conn);
            result.put("RspCode", "99");
            result.put("Message", "Unknown error");
            result.put("success", false);
            LOGGER.warning("Lỗi xử lý Fake-bank webhook: " + e.getMessage());
            return result;
        } finally {
            close(conn);
        }
    }

    public List<Map<String, Object>> getTransactionHistory(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Transactions trans : transactionDAO.findByUserId(userId)) {
            Map<String, Object> item = new HashMap<>();
            item.put("txnRef", trans.getTxnRef());
            item.put("status", statusName(trans));
            item.put("amount", trans.getAmount());
            item.put("paymentProvider", trans.getPaymentProvider());
            item.put("createdAt", trans.getCreatedAt() == null ? null : trans.getCreatedAt().toString());
            item.put("updatedAt", trans.getUpdatedAt() == null ? null : trans.getUpdatedAt().toString());
            if (trans.getPackageId() != null) {
                item.put("packageId", trans.getPackageId());
                ServicePackages pkg = packagesDAO.getPackageById(trans.getPackageId());
                if (pkg != null) {
                    item.put("packageName", pkg.getPackageName());
                }
            }
            result.add(item);
        }
        return result;
    }

    public Map<String, Object> getTransactionStatus(int userId, String txnRef) {
        if (txnRef == null || txnRef.isBlank()) {
            return null;
        }
        Transactions trans = transactionDAO.findByTxnRefAndUserId(txnRef, userId);
        if (trans == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("txnRef", trans.getTxnRef());
        result.put("status", statusName(trans));
        result.put("amount", trans.getAmount());
        result.put("packageId", trans.getPackageId());
        result.put("providerTransactionId", trans.getProviderTransactionId());
        if (trans.getPackageId() != null) {
            ServicePackages pkg = packagesDAO.getPackageById(trans.getPackageId());
            if (pkg != null) {
                result.put("packageName", pkg.getPackageName());
            }
        }
        return result;
    }

    static boolean matchesPaymentDetails(Transactions transaction, FakeBankWebhookDTO webhook) {
        return FakeBankConfig.MERCHANT_ID.equals(webhook.getMerchantId())
                && FakeBankConfig.CURRENCY.equals(webhook.getCurrency())
                && transaction.getProviderTransactionId() != null
                && transaction.getProviderTransactionId().equals(webhook.getPaymentId())
                && transaction.getAmount() != null
                && BigDecimal.valueOf(transaction.getAmount()).compareTo(webhook.getAmount()) == 0;
    }

    static boolean isValidWebhookPayload(FakeBankWebhookDTO webhook) {
        return isValidWebhook(webhook);
    }

    private static boolean isValidWebhook(FakeBankWebhookDTO webhook) {
        if (webhook == null
                || !notBlank(webhook.getEvent())
                || !notBlank(webhook.getEventId())
                || !notBlank(webhook.getPaymentId())
                || !notBlank(webhook.getMerchantId())
                || !notBlank(webhook.getMerchantReference())
                || webhook.getAmount() == null
                || webhook.getAmount().signum() <= 0
                || !("payment.succeeded".equals(webhook.getEvent())
                || "payment.failed".equals(webhook.getEvent()))
                || !FakeBankConfig.CURRENCY.equals(webhook.getCurrency())) {
            return false;
        }
        try {
            java.time.Instant.parse(webhook.getCreatedAt());
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private Map<String, Object> failure(Map<String, Object> result, String code, String message) {
        result.put("RspCode", code);
        result.put("Message", message);
        result.put("success", false);
        return result;
    }

    private Map<String, Object> rollbackFailure(Connection conn, Map<String, Object> result, String code, String message)
            throws SQLException {
        conn.rollback();
        return failure(result, code, message);
    }

    private String statusName(Transactions trans) {
        if (trans.getPaymentStatus() != null && !trans.getPaymentStatus().isBlank()) {
            return trans.getPaymentStatus();
        }
        return trans.getStatus() == 1 ? SUCCESS : trans.getStatus() == 2 ? FAILED : "PENDING";
    }

    private void activateVip(Connection conn, int userId, Integer packageId) {
        if (packageId == null) {
            throw new IllegalStateException("Giao dịch thiếu package_id");
        }
        ServicePackages pkg = packagesDAO.getPackageById(conn, packageId);
        if (pkg == null) {
            throw new IllegalStateException("Gói dịch vụ không tồn tại khi kích hoạt VIP");
        }

        UserServicesMD current = null;
        try {
            String sql = """
                SELECT end_date FROM user_services
                WHERE user_id = ? AND status = 1 AND end_date >= CURRENT_TIMESTAMP
                ORDER BY end_date DESC LIMIT 1
                """;
            try (var ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        current = new UserServicesMD();
                        var ts = rs.getTimestamp("end_date");
                        if (ts != null) {
                            current.setEndDate(ts.toLocalDateTime().toLocalDate());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LocalDate start = LocalDate.now();
        LocalDate endBase = start;
        if (current != null && current.getEndDate() != null && current.getEndDate().isAfter(start)) {
            endBase = current.getEndDate();
        }
        LocalDate end = endBase.plusDays(pkg.getDurationDays());

        userServicesMDDAO.deactivateActiveServices(conn, userId);
        userServicesMDDAO.insertUserService(conn, userId, packageId, start, end, 1);
    }

    private ServicePackageDTO toDto(ServicePackages pkg) {
        ServicePackageDTO dto = new ServicePackageDTO();
        dto.setId(pkg.getId());
        dto.setPackageName(pkg.getPackageName());
        dto.setTargetAudience(pkg.getTargetAudience());
        dto.setPrice(pkg.getPrice());
        dto.setDurationDays(pkg.getDurationDays());
        dto.setBenefitType(pkg.getBenifitType());
        dto.setDescription(pkg.getDescription());
        return dto;
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /** Legacy endpoint is kept only so old clients receive a clear migration message. */
    @Deprecated
    public void purchasepackage(int userId, int packageID) throws SQLException {
        throw new BusinessException.ValidationException("Vui lòng thanh toán qua cổng demo: POST /api/payment/create");
    }
}
