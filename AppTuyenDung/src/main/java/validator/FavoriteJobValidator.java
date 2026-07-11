package validator;

import dto.FavoriteJobDTO;

public class FavoriteJobValidator {

    public static void validate(FavoriteJobDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống.");
        }

        if (dto.getCandidateId() == null || dto.getCandidateId() <= 0) {
            throw new IllegalArgumentException("Ứng viên không hợp lệ.");
        }

        if (dto.getJobId() == null || dto.getJobId() <= 0) {
            throw new IllegalArgumentException("Công việc không hợp lệ.");
        }
    }
}