package service;

import dao.JobDAO;
import dto.JobDTO;
import dto.JobSearchDTO;
import dto.PageResponse;
import mapper.JobMapper;
import model.Job;
import validator.JobValidator;


import java.util.ArrayList;
import java.util.List;

public class JobService {

    private final JobDAO JobDAO;
    private final JobValidator validator;

    public JobService() {
        this.JobDAO = new JobDAO();
        this.validator = new JobValidator();
    }

    //thêm add xog trả về id cũng được
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
            jobDTOList.add(new JobDTO(x.getId(), x.getEmployerID().getId(), x.getTitle(), x.getDescription(), x.getMinSalary(), x.getMaxSalary(), x.getCurrency(), x.getLocation(), x.getExperience(), x.getQuantity(), x.getPostedAt(), x.getExpiredAt(), x.getApplicationDeadline(), x.getStatus().getValue(), x.getHiddenOnExpiry()));
        });
        return jobDTOList;
    }

    //    public List<JobDTO> getAllJobs() {
//
//        return JobDAO.getAll()
//                .stream()
//                .map(JobMapper::toDTO)
//                .toList();
//    }
    public PageResponse<JobDTO> search(JobSearchDTO searchDTO) {

        List<JobDTO> jobs = JobDAO.search(searchDTO)
                .stream()
                .map(JobMapper::toDTO)
                .toList();

        int totalItems = JobDAO.count(searchDTO);

        int totalPages = (int) Math.ceil((double) totalItems / searchDTO.getSize());

        return new PageResponse<>(
                jobs,
                searchDTO.getPage(),
                searchDTO.getSize(),
                totalPages,
                totalItems
        );
    }
}