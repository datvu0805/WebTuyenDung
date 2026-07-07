package service;

import dao.JobSkillDAO;
import model.JobSkills;

import java.util.List;

public class JobSkillService {

    private final JobSkillDAO jobSkillDAO = new JobSkillDAO();

    public void add(JobSkills jobSkill) {

        if (jobSkillDAO.exists(jobSkill.getJobID().getId(), jobSkill.getSkillID().getId())) {

            throw new IllegalArgumentException("Skill đã tồn tại trong Job.");
        }

        jobSkillDAO.add(jobSkill);
    }

    public void delete(JobSkills jobSkill) {
        jobSkillDAO.delete(jobSkill);
    }

    public List<JobSkills> getByJobId(int jobId) {
        return jobSkillDAO.getByJobId(jobId);
    }

    public List<JobSkills> getBySkillId(int skillId) {
        return jobSkillDAO.getBySkillId(skillId);
    }
}