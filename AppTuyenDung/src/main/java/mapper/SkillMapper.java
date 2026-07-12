package mapper;

import dto.SkillDTO;
import model.Skill;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SkillMapper {
    public static Skill map(ResultSet rs) throws SQLException {

        Skill skill = new Skill();

        skill.setId(rs.getInt("id"));
        skill.setSkillName(rs.getString("skill_name"));
        skill.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        skill.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return skill;
    }
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