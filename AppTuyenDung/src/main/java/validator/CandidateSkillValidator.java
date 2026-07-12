package validator;

import dto.CandidateSkillBatchDTO;
import dto.CandidateSkillDTO;

public class CandidateSkillValidator {

    public static void validate(CandidateSkillDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống.");
        }

        if (dto.getCandidateId() == null || dto.getCandidateId() <= 0) {
            throw new IllegalArgumentException("Ứng viên không hợp lệ.");
        }

        if (dto.getSkillId() == null || dto.getSkillId() <= 0) {
            throw new IllegalArgumentException("Kỹ năng không hợp lệ.");
        }
    }

    public static void validate(CandidateSkillBatchDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống.");
        }

        if (dto.getCandidateId() == null || dto.getCandidateId() <= 0) {
            throw new IllegalArgumentException("Ứng viên không hợp lệ.");
        }

        if (dto.getSkillIds() == null || dto.getSkillIds().isEmpty()) {
            throw new IllegalArgumentException("Danh sách kỹ năng không được để trống.");
        }

        for (Integer id : dto.getSkillIds()) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Kỹ năng không hợp lệ.");
            }
        }
    }
}