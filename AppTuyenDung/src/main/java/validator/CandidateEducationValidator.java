package validator;

import dto.CandidateEducationDTO;

public class CandidateEducationValidator {

    public static void validate(CandidateEducationDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống.");
        }

        if (dto.getCandidateId() <= 0) {
            throw new IllegalArgumentException("Ứng viên không hợp lệ.");
        }

        if (dto.getEducationLevelId() <= 0) {
            throw new IllegalArgumentException("Trình độ học vấn không hợp lệ.");
        }

        if (dto.getStartDate() != null && dto.getEndDate() != null) {

            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
            }
        }

        if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            throw new IllegalArgumentException("Mô tả không được vượt quá 255 ký tự.");
        }
    }
}
