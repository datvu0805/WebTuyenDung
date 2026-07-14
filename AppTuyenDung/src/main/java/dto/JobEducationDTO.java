package dto;

public class JobEducationDTO {

    private Integer jobId;
    private Integer educationLevelId;

    public JobEducationDTO() {
    }

    public JobEducationDTO(Integer jobId, Integer educationLevelId) {
        this.jobId = jobId;
        this.educationLevelId = educationLevelId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getEducationLevelId() {
        return educationLevelId;
    }

    public void setEducationLevelId(Integer educationLevelId) {
        this.educationLevelId = educationLevelId;
    }
}
