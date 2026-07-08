package dto;

public class JobSkillDTO {

    private Integer jobId;
    private Integer skillId;

    public JobSkillDTO() {
    }

    public JobSkillDTO(Integer jobId, Integer skillId) {
        this.jobId = jobId;
        this.skillId = skillId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }
}