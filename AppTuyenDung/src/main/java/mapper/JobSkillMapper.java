package mapper;

import dto.JobSkillDTO;
import model.Job;
import model.JobSkill;
import model.Skill;

public class JobSkillMapper {

    public static JobSkill toEntity(JobSkillDTO dto) {

        if (dto == null) {
            return null;
        }

        JobSkill jobSkill = new JobSkill();

        jobSkill.setJobID(new Job(dto.getJobId()));
        jobSkill.setSkillID(new Skill(dto.getSkillId()));

        return jobSkill;
    }

    public static JobSkillDTO toDTO(JobSkill jobSkill) {

        if (jobSkill == null) {
            return null;
        }

        return new JobSkillDTO(
                jobSkill.getJobID().getId(),
                jobSkill.getSkillID().getId()
        );
    }

}