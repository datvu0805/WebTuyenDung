package service;

import dao.EducationLevelDAO;
import dao.JobDAO;
import dao.JobEducationDAO;
import dto.JobEducationDTO;
import model.EducationLevel;
import model.Job;
import validator.JobEducationValidator;

import java.util.List;

public class JobEducationService {

    private final JobEducationDAO jobEducationDAO = new JobEducationDAO();
    private final JobDAO jobDAO = new JobDAO();
    private final EducationLevelDAO educationLevelDAO = new EducationLevelDAO();

    public void add(JobEducationDTO dto) {

        JobEducationValidator.validate(dto);

        Job job = jobDAO.getById(dto.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        EducationLevel educationLevel = educationLevelDAO.getById(dto.getEducationLevelId());
        if (educationLevel == null) {
            throw new IllegalArgumentException("Trình độ học vấn không tồn tại.");
        }

        if (jobEducationDAO.exists(job.getId(), educationLevel.getId())) {
            throw new IllegalArgumentException("Job đã yêu cầu trình độ học vấn này.");
        }

        jobEducationDAO.add(job, educationLevel);
    }

    public void delete(JobEducationDTO dto) {

        JobEducationValidator.validate(dto);

        Job job = jobDAO.getById(dto.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        EducationLevel educationLevel = educationLevelDAO.getById(dto.getEducationLevelId());
        if (educationLevel == null) {
            throw new IllegalArgumentException("Trình độ học vấn không tồn tại.");
        }

        jobEducationDAO.delete(job, educationLevel);
    }

    public List<EducationLevel> getEducationLevelsByJob(int jobId) {

        Job job = jobDAO.getById(jobId);

        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        return jobEducationDAO.getEducationLevelsByJob(job);
    }
}
