package validator;

import dto.PurchaseRequestDTO;
import exception.BusinessException;

public class PaymentValidator {
    public static void vadidatePurchaseInput(PurchaseRequestDTO dto){
        if (dto == null){
            throw new BusinessException.ValidationException("Dữ liwwuj yêu cầu không được để trống!");

        }
        if(dto.getUserID() == null){
            throw new BusinessException.ValidationException("Mã người dùng không được để trống!");
        }
        if (dto.getUserID() <= 0){
            throw new BusinessException.ValidationException("mã người dùng phải la số nguyên dương!");
        }
        if(dto.getPackageID() <= 0){
            throw new BusinessException.ValidationException("Mã gói dịch vụ phải là số nguyên dương");
        }
    }
}
