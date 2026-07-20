package validator;

import dto.PurchaseRequestDTO;
import exception.BusinessException;

public class PaymentValidator {
    public static void vadidatePurchaseInput(PurchaseRequestDTO dto){
        if (dto == null){
            throw new BusinessException.ValidationException("Dữ liệu yêu cầu không được để trống!");
        }
        // userID có thể null ở request body — servlet sẽ set từ session
        if (dto.getUserID() != null && dto.getUserID() <= 0){
            throw new BusinessException.ValidationException("Mã người dùng phải là số nguyên dương!");
        }
        if (dto.getPackageID() == null || dto.getPackageID() <= 0){
            throw new BusinessException.ValidationException("Mã gói dịch vụ phải là số nguyên dương");
        }
    }
}
