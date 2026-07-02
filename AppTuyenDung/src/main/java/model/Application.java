package model;

import java.io.PrintWriter;
import java.time.LocalDateTime;

// bảng đơn ứng tuyển
public class Application extends BaseEntity{
    private int candidateID;
    private int jodID;
    private int cvID;
    private LocalDateTime appliedAt;
    private String coverLetter;
    private String description;
    private int status;

    public Application() {
    }

    public Application(int candidateID, int jodID, int cvID, LocalDateTime appliedAt, String coverLetter, String description, int status) {
        this.candidateID = candidateID;
        this.jodID = jodID;
        this.cvID = cvID;
        this.appliedAt = appliedAt;
        this.coverLetter = coverLetter;
        this.description = description;
        this.status = status;
    }

    public Application(int id, int candidateID, int jodID, int cvID, LocalDateTime appliedAt, String coverLetter, String description, int status) {
        super(id);
        this.candidateID = candidateID;
        this.jodID = jodID;
        this.cvID = cvID;
        this.appliedAt = appliedAt;
        this.coverLetter = coverLetter;
        this.description = description;
        this.status = status;
    }

    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
        this.candidateID = candidateID;
    }

    public int getJodID() {
        return jodID;
    }

    public void setJodID(int jodID) {
        this.jodID = jodID;
    }

    public int getCvID() {
        return cvID;
    }

    public void setCvID(int cvID) {
        this.cvID = cvID;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
