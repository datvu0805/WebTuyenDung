package mapper;

import dto.CandidateEducationDTO;
import model.CandidateEducation;
import model.Candidates;
import model.EducationLevel;

public class CandidateEducationMapper {

    public static CandidateEducationDTO toDTO(CandidateEducation entity) {

        if (entity == null) {
            return null;
        }

        return new CandidateEducationDTO(
                entity.getId(),
                entity.getCandidateID().getId(),
                entity.getEducationLevelID().getId(),
                entity.getSchoolName(),
                entity.getMajor(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getGpa(),
                entity.getDescription()
        );
    }

    public static CandidateEducation toEntity(CandidateEducationDTO dto) {

        if (dto == null) {
            return null;
        }

        return new CandidateEducation(
                dto.getId(),
                new Candidates(dto.getCandidateId()),
                new EducationLevel(dto.getEducationLevelId()),
                dto.getSchoolName(),
                dto.getMajor(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getGpa(),
                dto.getDescription()
        );
    }
}
