package service;

import dao.JobDAO;
import dto.JobDTO;
import mapper.JobMapper;
import model.Job;
import validator.JobValidator;

import model.JobSkill;
import model.Skill;

import java.util.ArrayList;
import java.util.List;

public class JobService {

    private final JobDAO JobDAO;
    private final JobValidator validator;

    public JobService() {
        this.JobDAO = new JobDAO();
        this.validator = new JobValidator();
    }


    public void addJob(JobDTO dto) {

        List<String> errors = validator.validateJob(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Job job = JobMapper.toEntity(dto);

        JobDAO.add(job);
    }


    public void updateJob(int id, JobDTO dto) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        List<String> errors = validator.validateJob(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Job job = JobMapper.toEntity(dto);
        job.setId(id);

        JobDAO.update(job);
    }


    public void deleteJob(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        JobDAO.delete(id);
    }


    public JobDTO getJobById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        Job job = JobDAO.getById(id);

        return JobMapper.toDTO(job);
    }


    public List<JobDTO> getAllJobs() {
        List<JobDTO> jobDTOList = new ArrayList<>();
        JobDTO jobDTO1 = new JobDTO();
        List<Job> jobList = JobDAO.getAll();
         jobList.stream().forEach(x -> {
            jobDTOList.add(new JobDTO(x.getId(),x.getTitle(), x.getDescription(), x.getSalary(), x.getLocation(), x.getExperience(), x.getQuantity(),x.getPostedAt(),x.getExpiredAt(),x.getApplicationDeadline(),x.getStatus(),x.getHiddenOnExpiry()));
        });
         return jobDTOList;
    }
}