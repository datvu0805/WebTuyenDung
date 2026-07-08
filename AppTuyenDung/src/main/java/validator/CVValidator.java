package validator;

import dto.UploadCVDTO;
import javax.servlet.http.Part;

public class CVValidator {
    private static final long MAX_CV_SIZE = 2 * 1024 * 1024;       // 2MB
//    private static final long MAX_AVATAR_SIZE = 1 * 1024 * 1024;   // 1MB

    public static String validate(UploadCVDTO dto) {
        if (dto.getCandidateId() == null || dto.getCandidateId().trim().isEmpty()) {
            return "Lỗi: candidate_id không được để trống!";
        }
        try {
            Integer.parseInt(dto.getCandidateId());
        } catch (NumberFormatException e) {
            return "Lỗi: candidate_id phải là một số nguyên hợp lệ!";
        }

        if (dto.getCvTitle() == null || dto.getCvTitle().trim().isEmpty()) {
            return "Lỗi: cv_title không được để trống!";
        }

        Part fileCV = dto.getFileCV();
        if (fileCV == null || fileCV.getSize() == 0) {
            return "Lỗi: Vui lòng chọn tệp tin CV cần nộp!";
        }

        if (fileCV.getSize() > MAX_CV_SIZE) {
            return "Lỗi: Kích thước file CV vượt quá giới hạn (Tối đa 2MB)!";
        }

        String cvContentType = fileCV.getContentType();
        if (cvContentType == null || (!cvContentType.equals("application/pdf")
                && !cvContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
            return "Lỗi: Định dạng file CV không hợp lệ! Hệ thống chỉ chấp nhận file .pdf hoặc .xlsx (Excel).";
        }

//        Part fileAvatar = dto.getFileAvatar();
//        if (fileAvatar != null && fileAvatar.getSize() > 0) {
//            if (fileAvatar.getSize() > MAX_AVATAR_SIZE) {
//                return "Lỗi: Kích thước ảnh đại diện vượt quá giới hạn (Tối đa 1MB)!";
//            }
//            String avatarContentType = fileAvatar.getContentType();
//            if (avatarContentType == null || !avatarContentType.startsWith("image/")) {
//                return "Lỗi: Ảnh đại diện phải là file hình ảnh mẫu (png, jpg, jpeg).";
//            }
//        }
        return null;
    }
}