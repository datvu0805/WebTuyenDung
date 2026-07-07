package validator;

import exception.BusinessException;

public class ApplicationValidator {
    public static void validateUpdateStatus(String idRaw, String statusRaw){
        if(idRaw == null || idRaw.trim().isEmpty()){
            throw new BusinessException("ID đơn ứng tuyển không được bỏ trống");

        }
        if(statusRaw == null || statusRaw.trim().isEmpty()){
            throw new BusinessException("Trạng thái cập nhật không được để trong!");
       }

        try {
            int id = Integer.parseInt(idRaw);
            if(id <= 0){
                throw new BusinessException("ID đơn ứng tuyển là so nguyên dương!");
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("ID đơn ứng tuyển phải l số nguyên dương!");
        }

        try {
            int status = Integer.parseInt(statusRaw);
            if (status < 0 || status >3){
                throw new BusinessException("Trạng thái không hợp lệ!");
            }
        }catch (NumberFormatException e){
            throw new BusinessException("Mã trạng thái phải là một số nguyên!");
        }
    }
    // ktra dữ liệu ứng viên nộp đơn
    public static void validateSubmit(String candidateRaw, String jobIDRaw, String cvIdRaw, String coverLetter){
        if (candidateRaw == null || candidateRaw.trim().isEmpty()){
            throw new BusinessException("Thông tin ứng viên không được để trống!");
        }
        if (jobIDRaw == null || jobIDRaw.trim().isEmpty()){
            throw new BusinessException("Công việc ứng tuyển không được để trống!");
        }
        if (cvIdRaw == null || cvIdRaw.trim().isEmpty()){
            throw new BusinessException("Vui lòng chọn hoặc tải lên CV trước khi nộp! ");
        }
        try {
            if (Integer.parseInt(candidateRaw) <= 0 || Integer.parseInt(jobIDRaw) <= 0 || Integer.parseInt(cvIdRaw) <= 0){
                throw new BusinessException("Mã id là số nguyên dương!");
            }
        }catch (NumberFormatException e){
            throw new BusinessException("Các mã id phải đúng định dạng!");
        }
        if (coverLetter != null && coverLetter.length() > 2000){
            throw new BusinessException("Thư giới thiệu không được vượt quá 2000 ký tự");
        }
    }
}
