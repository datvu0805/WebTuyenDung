package mapper;

import dto.SkillDTO;
import model.Skill;

public class SkillMapper {

    public static Skill toEntity(SkillDTO dto) {

        if (dto == null) {
            return null;
        }

        Skill skill = new Skill();
        if (dto.getId() != null) {
            skill.setId(dto.getId());
        }

        skill.setSkillName(dto.getSkillName());

        return skill;
    }

    public static SkillDTO toDTO(Skill skill) {

        if (skill == null) {
            return null;
        }

        return new SkillDTO(skill.getId(), skill.getSkillName());
    }
}