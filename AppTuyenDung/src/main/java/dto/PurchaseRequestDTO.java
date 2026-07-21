package dto;

import model.Users;

// yêu cầu mua gói VIP (userID lấy từ session, không tin client)
public class PurchaseRequestDTO {
    private Integer userID;
    private Integer packageID;

    public PurchaseRequestDTO() {
    }

    public PurchaseRequestDTO(Integer userID, Integer packageID) {
        this.userID = userID;
        this.packageID = packageID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getPackageID() {
        return packageID;
    }

    public void setPackageID(Integer packageID) {
        this.packageID = packageID;
    }

    @Override
    public String toString() {
        return "PurchaseRequestDTO{userID=" + userID + ", packageID=" + packageID + '}';
    }
}
