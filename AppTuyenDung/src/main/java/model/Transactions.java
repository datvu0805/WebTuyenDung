package model;


// lịch sử giao dịch
public class Transactions extends BaseEntity{
    private User userID;
    private String transactionType;
    private Double amount;
    private int status;
    private String conntent;

    public Transactions() {
    }

    public Transactions(User userID, String transactionType, Double amount, int status, String conntent) {
        this.userID = userID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.conntent = conntent;
    }

    public Transactions(int id, User userID, String transactionType, Double amount, int status, String conntent) {
        super(id);
        this.userID = userID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.conntent = conntent;
    }

    public User getUserID() {
        return userID;
    }

    public void setUserID(User userID) {
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

    public String getConntent() {
        return conntent;
    }

    public void setConntent(String conntent) {
        this.conntent = conntent;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
