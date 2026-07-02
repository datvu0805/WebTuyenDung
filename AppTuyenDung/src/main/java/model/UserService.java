package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

// lịch sử đăng kí dịch vu
public class UserService extends BaseEntity{
    private int userID;
    private int packageID;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public UserService() {
    }

    public UserService(int userID, int packageID, LocalDate startDate, LocalDate endDate, String status) {
        this.userID = userID;
        this.packageID = packageID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UserService(int id, int userID, int packageID, LocalDate startDate, LocalDate endDate, String status) {
        super(id);
        this.userID = userID;
        this.packageID = packageID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getUserID() {return userID;}

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
