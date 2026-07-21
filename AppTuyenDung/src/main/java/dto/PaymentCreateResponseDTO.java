package dto;

public class PaymentCreateResponseDTO {
    private String paymentUrl;
    private String txnRef;
    private long amount;
    private int packageId;
    private String packageName;

    public PaymentCreateResponseDTO() {
    }

    public PaymentCreateResponseDTO(String paymentUrl, String txnRef, long amount, int packageId, String packageName) {
        this.paymentUrl = paymentUrl;
        this.txnRef = txnRef;
        this.amount = amount;
        this.packageId = packageId;
        this.packageName = packageName;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
