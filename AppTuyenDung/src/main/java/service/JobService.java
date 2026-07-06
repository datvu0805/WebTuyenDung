package service;

import dao.JobsDAO;
import dto.JobDTO;
import mapper.JobMapper;
import model.Jobs;
import validator.JobValidator;

import model.JobSkills;
import model.Skills;
import java.util.List;

public class JobService {

    private final JobsDAO jobsDAO;
    private final JobValidator validator;

    public JobService() {
        this.jobsDAO = new JobsDAO();
        this.validator = new JobValidator();
    }


    public void addJob(JobDTO dto) {

        List<String> errors = validator.validateJob(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Jobs job = JobMapper.toEntity(dto);

        jobsDAO.add(job);
    }


    public void updateJob(int id, JobDTO dto) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        List<String> errors = validator.validateJob(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Jobs job = JobMapper.toEntity(dto);
        job.setId(id);

        jobsDAO.update(job);
    }


    public void deleteJob(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        jobsDAO.delete(id);
    }


    public Jobs getJobById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        return jobsDAO.getById(id);
    }


    public List<Jobs> getAllJobs() {
        return jobsDAO.getAll();
    }


    public void addSkillToJob(int jobId, int skillId) {

        if (jobId <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        if (skillId <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        jobsDAO.addSkill(new Jobs(jobId), new Skills(skillId));
    }


    public void removeSkillFromJob(int jobId, int skillId) {

        if (jobId <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        if (skillId <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        jobsDAO.deleteSkill(new Jobs(jobId), new Skills(skillId));
    }


    public List<JobSkills> getSkillsByJob(int jobId) {

        if (jobId <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        return jobsDAO.getSkillsByJob(jobId);
    }
}