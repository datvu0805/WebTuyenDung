package validator;

import dto.JobSkillDTO;

import java.util.ArrayList;
import java.util.List;

public class JobSkillValidator {
// validator là kiểm tra với nội dung người nhập vào rồi so sánh vs điều kiện
    public List<String> validate(JobSkillDTO dto) {

        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Thông tin JobSkill không được để trống");
            return errors;
        }

        if (dto.getJobId() == null || dto.getJobId() <= 0) {
            errors.add("ID công việc không hợp lệ");
        }

        if (dto.getSkillId() == null || dto.getSkillId() <= 0) {
            errors.add("ID kỹ năng không hợp lệ");
        }

        return errors;
    }

}