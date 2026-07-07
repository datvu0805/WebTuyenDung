package dto;

import javax.servlet.http.Part;

public class UploadCVDTO {
    private String candidateId;
    private String cvTitle;
    private String description;
    private String version;
    private Part fileCV;
//    private Part fileAvatar;

    public UploadCVDTO() {}

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    public String getCvTitle() { return cvTitle; }
    public void setCvTitle(String cvTitle) { this.cvTitle = cvTitle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Part getFileCV() { return fileCV; }
    public void setFileCV(Part fileCV) { this.fileCV = fileCV; }
//    public Part getFileAvatar() { return fileAvatar; }
//    public void setFileAvatar(Part fileAvatar) { this.fileAvatar = fileAvatar; }
}