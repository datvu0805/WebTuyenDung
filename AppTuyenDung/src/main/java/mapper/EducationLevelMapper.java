package mapper;

import dto.EducationLevelDTO;
import model.EducationLevel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EducationLevelMapper {

    public static EducationLevel map(ResultSet rs) throws SQLException {

        EducationLevel entity = new EducationLevel();

        entity.setId(rs.getInt("id"));
        entity.setLevelName(rs.getString("level_name"));

        return entity;
    }

    public static EducationLevel toEntity(EducationLevelDTO dto) {

        if (dto == null) {
            return null;
        }

        EducationLevel entity = new EducationLevel();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }

        entity.setLevelName(dto.getLevelName());

        return entity;
    }

    public static EducationLevelDTO toDTO(EducationLevel entity) {

        if (entity == null) {
            return null;
        }

        return new EducationLevelDTO(entity.getId(), entity.getLevelName());
    }
}
