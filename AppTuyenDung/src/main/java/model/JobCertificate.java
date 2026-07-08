package model;


// Bảng chứng chỉ yêu cầu của công việc
public class JobCertificate extends BaseEntity{
    private Job jobID;
    private Certificate certificatesID;

// Điểm tối thiểu yêu cầu
    private String requiredScore;

    public JobCertificate(Job jobID, Certificate certificatesID, String requiredScore) {
        this.jobID = jobID;
        this.certificatesID = certificatesID;
        this.requiredScore = requiredScore;
    }

    public JobCertificate(int id, Job jobID, Certificate certificatesID, String requiredScore) {
        super(id);
        this.jobID = jobID;
        this.certificatesID = certificatesID;
        this.requiredScore = requiredScore;
    }

    public Job getJobID() {
        return jobID;
    }

    public void setJobID(Job jobID) {
        this.jobID = jobID;
    }

    public Certificate getCertificatesID() {
        return certificatesID;
    }

    public void setCertificatesID(Certificate certificatesID) {
        this.certificatesID = certificatesID;
    }

    public String getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(String requiredScore) {
        this.requiredScore = requiredScore;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
