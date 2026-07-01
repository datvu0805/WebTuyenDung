package model;


import constant.ScoreType;

// Bảng chứng chỉ yêu cầu của công việc
public class JobCertificates extends BaseEntity{
    private Jobs jobID;
    private Certificates certificatesID;

// Điểm tối thiểu yêu cầu
    private String requiredScore;

    public JobCertificates(Jobs jobID, Certificates certificatesID, String requiredScore) {
        this.jobID = jobID;
        this.certificatesID = certificatesID;
        this.requiredScore = requiredScore;
    }

    public JobCertificates(int id, Jobs jobID, Certificates certificatesID, String requiredScore) {
        super(id);
        this.jobID = jobID;
        this.certificatesID = certificatesID;
        this.requiredScore = requiredScore;
    }

    public Jobs getJobID() {
        return jobID;
    }

    public void setJobID(Jobs jobID) {
        this.jobID = jobID;
    }

    public Certificates getCertificatesID() {
        return certificatesID;
    }

    public void setCertificatesID(Certificates certificatesID) {
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
