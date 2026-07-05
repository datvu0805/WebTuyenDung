package model.main.java.model;

import java.time.LocalDate;

// lịch sử đăng kí dịch vu
public class UserServicesMD extends BaseEntity {
    private Users userID;
    private ServicePackages packageID;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public UserServicesMD() {
    }

    public UserServicesMD(Users userID, ServicePackages packageID, LocalDate startDate, LocalDate endDate, String status) {
        this.userID = userID;
        this.packageID = packageID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UserServicesMD(int id, Users userID, ServicePackages packageID, LocalDate startDate, LocalDate endDate, String status) {
        super(id);
        this.userID = userID;
        this.packageID = packageID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Users getUserID() {return userID;}

    public void setUserID(Users userID) {
        this.userID = userID;
    }

    public ServicePackages getPackageID() {
        return packageID;
    }

    public void setPackageID(ServicePackages packageID) {
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
