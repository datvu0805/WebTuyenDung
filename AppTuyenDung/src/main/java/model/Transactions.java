package model;

// lịch sử giao dịch
public class Transactions extends BaseEntity {
    private int userID;
    private String transactionType;
    private Double amount;
    private int status; // 0 pending, 1 success, 2 failed
    private String paymentStatus;
    private String content;
    private Integer packageId;
    private String txnRef;
    private String paymentProvider;
    private String providerTransactionId;

    public Transactions() {
    }

    public Transactions(int userID, String transactionType, Double amount, int status, String content) {
        this.userID = userID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.content = content;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    @Override
    public String getInfo() {
        return "Txn#" + getId() + " user=" + userID + " status=" + status;
    }
}
