package dto;

public class FavoriteJobDTO {

    private Integer candidateId;
    private Integer jobId;

    public FavoriteJobDTO() {
    }

    public FavoriteJobDTO(Integer candidateId, Integer jobId) {
        this.candidateId = candidateId;
        this.jobId = jobId;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
}