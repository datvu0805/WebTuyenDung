package dto;

import java.util.List;

public class JobCertificateBatchDTO {

    private Integer jobId;
    private List<Integer> certificateIds;

    public JobCertificateBatchDTO() {
    }

    public JobCertificateBatchDTO(Integer jobId, List<Integer> certificateIds) {
        this.jobId = jobId;
        this.certificateIds = certificateIds;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public List<Integer> getCertificateIds() {
        return certificateIds;
    }

    public void setCertificateIds(List<Integer> certificateIds) {
        this.certificateIds = certificateIds;
    }
}