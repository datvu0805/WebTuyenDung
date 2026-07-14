package dto;

// Dùng cho response GET /job-certificates/job — trả kèm điểm tối thiểu employer yêu cầu (job_certificates.required_score)
public class JobCertificateRequirementDTO {

    private int certificateId;
    private String certificateName;
    private String scoreType;
    private String requiredScore;

    public JobCertificateRequirementDTO() {
    }

    public JobCertificateRequirementDTO(int certificateId, String certificateName, String scoreType, String requiredScore) {
        this.certificateId = certificateId;
        this.certificateName = certificateName;
        this.scoreType = scoreType;
        this.requiredScore = requiredScore;
    }

    public int getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(int certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public String getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(String requiredScore) {
        this.requiredScore = requiredScore;
    }
}
