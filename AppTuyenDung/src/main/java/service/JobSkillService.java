package service;

import dao.JobSkillDAO;
import model.JobSkill;

import java.util.List;

public class JobSkillService {

    private final JobSkillDAO jobSkillDAO = new JobSkillDAO();

    public void add(JobSkill jobSkill) {

        if (jobSkillDAO.exists(jobSkill.getJobID().getId(), jobSkill.getSkillID().getId())) {

            throw new IllegalArgumentException("Skill đã tồn tại trong Job.");
        }

        jobSkillDAO.add(jobSkill);
    }

    public void delete(JobSkill jobSkill) {
        jobSkillDAO.delete(jobSkill);
    }

    public List<JobSkill> getByJobId(int jobId) {
        return jobSkillDAO.getByJobId(jobId);
    }

    public List<JobSkill> getBySkillId(int skillId) {
        return jobSkillDAO.getBySkillId(skillId);
    }
}