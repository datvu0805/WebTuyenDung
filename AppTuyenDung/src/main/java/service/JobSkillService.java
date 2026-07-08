package service;

import dao.JobSkillDAO;
import dto.JobSkillDTO;
import mapper.JobSkillMapper;
import model.JobSkill;
import validator.JobSkillValidator;

import java.util.ArrayList;
import java.util.List;

public class JobSkillService {

    private final JobSkillDAO jobSkillDAO;
    private final JobSkillValidator validator;

    public JobSkillService() {

        this.jobSkillDAO = new JobSkillDAO();
        this.validator = new JobSkillValidator();
    }

    public void add(JobSkillDTO dto) {

        List<String> errors = validator.validate(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }
// .exists là lấy ra từ dao mục đích là so sánh xem kĩ năng đó đã được gán vào job đó chưa rồi nới add
        if (jobSkillDAO.exists(dto.getJobId(), dto.getSkillId())) {
            throw new IllegalArgumentException("Kỹ năng đã được gán cho công việc.");
        }

        jobSkillDAO.add(JobSkillMapper.toEntity(dto));
    }

    public void delete(JobSkillDTO dto) {

        List<String> errors = validator.validate(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        jobSkillDAO.delete(JobSkillMapper.toEntity(dto));
    }

    public List<JobSkillDTO> getByJobId(int jobId) {

        if (jobId <= 0) {
            throw new IllegalArgumentException("ID công việc không hợp lệ");
        }

        List<JobSkillDTO> result = new ArrayList<>();

        for (JobSkill item : jobSkillDAO.getByJobId(jobId)) {
            result.add(JobSkillMapper.toDTO(item));
        }

        return result;
    }

    public List<JobSkillDTO> getBySkillId(int skillId) {

        if (skillId <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        List<JobSkillDTO> result = new ArrayList<>();

        for (JobSkill item : jobSkillDAO.getBySkillId(skillId)) {
            result.add(JobSkillMapper.toDTO(item));
        }

        return result;
    }

}