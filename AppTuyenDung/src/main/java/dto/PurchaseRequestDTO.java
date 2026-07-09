package dto;

import model.Users;

// yêu cầu mua hàng
public class PurchaseRequestDTO {
    private Integer userID;
    private Integer packageID;

    public PurchaseRequestDTO(Integer userID, Integer packageiD) {
        this.userID = userID;
        this.packageID = packageiD;
    }

    public PurchaseRequestDTO() {
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
        return "PurchaseRequestDTO{" +
                "userID=" + userID +
                ", packageiD=" + packageID +
                '}';
    }
}
