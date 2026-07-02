package dto;

import java.time.LocalTime;

// quản lý đơn ứng tuyển
public class ApplicationDTO {
    private int applicationID;
    private String candidateName;
    private String jobTitle;
    private String cvTitle;
    private String fileUrl;
    private LocalTime applieAt;
    private int status;

    public ApplicationDTO() {
    }

    public ApplicationDTO(int applicationID, String candidateName, String jobTitle, String cvTitle, String fileUrl, LocalTime applieAt, int status) {
        this.applicationID = applicationID;
        this.candidateName = candidateName;
        this.jobTitle = jobTitle;
        this.cvTitle = cvTitle;
        this.fileUrl = fileUrl;
        this.applieAt = applieAt;
        this.status = status;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCvTitle() {
        return cvTitle;
    }

    public void setCvTitle(String cvTitle) {
        this.cvTitle = cvTitle;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalTime getApplieAt() {
        return applieAt;
    }

    public void setApplieAt(LocalTime applieAt) {
        this.applieAt = applieAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ApplicationDTO{" +
                "applicationID=" + applicationID +
                ", candidateName='" + candidateName + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", cvTitle='" + cvTitle + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", applieAt=" + applieAt +
                ", status=" + status +
                '}';
    }
}
