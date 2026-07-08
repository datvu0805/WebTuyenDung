package model;

// class CV của ứng viên
public class CV extends BaseEntity{

    private Candidates candidateId;
    private String cvTitle;
    private String fileUrl;

    private String description;
    private String version;

    public CV() {
    }


    public CV(Candidates candidateId, String cvTitle, String fileUrl, String description, String version) {
        this.candidateId = candidateId;
        this.cvTitle = cvTitle;
        this.fileUrl = fileUrl;
        this.description = description;
        this.version = version;
    }

    public CV(int id, Candidates candidateId, String cvTitle, String fileUrl, String avatarURl, String description, String version) {
        super(id);
        this.candidateId = candidateId;
        this.cvTitle = cvTitle;
        this.fileUrl = fileUrl;
        this.description = description;
        this.version = version;
    }

    public int getCandidateId() {
        return candidateId.getId();
    }

    public void setCandidateId(Candidates candidateId) {
        this.candidateId = candidateId;
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



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
