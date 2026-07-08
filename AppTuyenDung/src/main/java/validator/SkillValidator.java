package validator;

import dto.SkillDTO;

import java.util.ArrayList;
import java.util.List;

public class SkillValidator {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<String> validateSkill(SkillDTO dto) {

        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Thông tin kỹ năng không được để trống");
            return errors;
        }

        String skillName = dto.getSkillName();

        // Không được để trống
        if (isEmpty(skillName)) {
            errors.add("Tên kỹ năng không được để trống");
            return errors;
        }

        // Không được có khoảng trắng đầu/cuối
        if (!skillName.equals(skillName.trim())) {
            errors.add("Tên kỹ năng không được có khoảng trắng ở đầu hoặc cuối");
        }

        skillName = skillName.trim();

        // Độ dài tối thiểu
        if (skillName.length() < MIN_LENGTH) {
            errors.add("Tên kỹ năng phải có ít nhất " + MIN_LENGTH + " ký tự");
        }

        // Độ dài tối đa
        if (skillName.length() > MAX_LENGTH) {
            errors.add("Tên kỹ năng tối đa " + MAX_LENGTH + " ký tự");
        }

        // Không có nhiều khoảng trắng liên tiếp
        if (skillName.matches(".*\\s{2,}.*")) {
            errors.add("Tên kỹ năng không được chứa nhiều khoảng trắng liên tiếp");
        }

        // Chỉ cho phép chữ, số, khoảng trắng và + # . -
        if (!skillName.matches("^[a-zA-ZÀ-ỹ0-9+#.\\-\\s]+$")) {
            errors.add("Tên kỹ năng chứa ký tự không hợp lệ");
        }

        return errors;
    }
}