package model.main.java.model;


// lịch sử giao dịch
public class Transactions extends BaseEntity {
    private Users userID;
    private String transactionType;
    private Double amount;
    private int status;
    private String content;

    public Transactions() {
    }

    public Transactions(Users userID, String transactionType, Double amount, int status, String conntent) {
        this.userID = userID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.content = conntent;
    }

    public Transactions(int id, Users userID, String transactionType, Double amount, int status, String conntent) {
        super(id);
        this.userID = userID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.content = conntent;
    }

    public Users getUserID() {
        return userID;
    }

    public void setUserID(Users userID) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String conntent) {
        this.content = conntent;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
