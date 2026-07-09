package service;

import dao.SkillDAO;
import dto.SkillDTO;
import mapper.SkillMapper;
import model.Skill;
import validator.SkillValidator;

import java.util.ArrayList;
import java.util.List;

public class SkillService {

    private final SkillDAO skillDAO;
    private final SkillValidator validator;

    public SkillService() {
        this.skillDAO = new SkillDAO();
        this.validator = new SkillValidator();
    }

    public void addSkill(SkillDTO dto) {

        List<String> errors = validator.validateSkill(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Skill skill = SkillMapper.toEntity(dto);

        skillDAO.add(skill);
    }

    public void updateSkill(int id, SkillDTO dto) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        List<String> errors = validator.validateSkill(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Skill skill = SkillMapper.toEntity(dto);
        skill.setId(id);

        skillDAO.update(skill);
    }

    public void deleteSkill(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        skillDAO.delete(id);
    }

    public SkillDTO getSkillById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        Skill skill = skillDAO.getById(id);

        return SkillMapper.toDTO(skill);
    }

    public List<SkillDTO> getAllSkills() {

        List<SkillDTO> skillDTOList = new ArrayList<>();

        List<Skill> skillList = skillDAO.getAll();

        skillList.forEach(x -> skillDTOList.add(new SkillDTO(x.getId(), x.getSkillName())));

        return skillDTOList;
    }
}