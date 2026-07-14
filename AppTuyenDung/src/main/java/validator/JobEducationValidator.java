package validator;

import dto.JobEducationDTO;

public class JobEducationValidator {

    public static void validate(JobEducationDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống.");
        }

        if (dto.getJobId() == null || dto.getJobId() <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ.");
        }

        if (dto.getEducationLevelId() == null || dto.getEducationLevelId() <= 0) {
            throw new IllegalArgumentException("ID trình độ học vấn không hợp lệ.");
        }
    }
}
