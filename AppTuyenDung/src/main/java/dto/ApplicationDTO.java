package dto;

import model.Application;
import model.Candidates;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// quản lý đơn ứng tuyển
public class ApplicationDTO {
    private Application applicationID;
    private Candidates candidateName;
    private String jobTitle;
    private String cvTitle;
    private String fileUrl;
    private LocalDateTime applieAt;
    private int status;
    private List<String> attachedCertificates; // tên chứng chỉ candidate gắn vào CV dùng ứng tuyển
    private List<String> attachedEducations;   // tên học vấn candidate gắn vào CV dùng ứng tuyển

    public ApplicationDTO() {
    }

    public ApplicationDTO(Application applicationID, Candidates candidateName, String jobTitle, String cvTitle, String fileUrl, LocalDateTime applieAt, int status) {
        this.applicationID = applicationID;
        this.candidateName = candidateName;
        this.jobTitle = jobTitle;
        this.cvTitle = cvTitle;
        this.fileUrl = fileUrl;
        this.applieAt = applieAt;
        this.status = status;
    }

    public List<String> getAttachedCertificates() {
        return attachedCertificates;
    }

    public void setAttachedCertificates(List<String> attachedCertificates) {
        this.attachedCertificates = attachedCertificates;
    }

    public List<String> getAttachedEducations() {
        return attachedEducations;
    }

    public void setAttachedEducations(List<String> attachedEducations) {
        this.attachedEducations = attachedEducations;
    }

    public Application getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(Application applicationID) {
        this.applicationID = applicationID;
    }

    public Candidates getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(Candidates candidateName) {
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

    public LocalDateTime getApplieAt() {
        return applieAt;
    }

    public void setApplieAt(LocalDateTime applieAt) {
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
                ", candidateName=" + candidateName +
                ", jobTitle='" + jobTitle + '\'' +
                ", cvTitle='" + cvTitle + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", applieAt=" + applieAt +
                ", status=" + status +
                '}';
    }
}
