package dto;

public class JobCertificateDTO {

    private Integer jobId;
    private Integer certificateId;
    private String requiredScore;

    public JobCertificateDTO() {
    }

    public JobCertificateDTO(Integer jobId, Integer certificateId, String requiredScore) {
        this.jobId = jobId;
        this.certificateId = certificateId;
        this.requiredScore = requiredScore;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Integer certificateId) {
        this.certificateId = certificateId;
    }

    public String getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(String requiredScore) {
        this.requiredScore = requiredScore;
    }
}