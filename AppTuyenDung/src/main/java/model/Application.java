package model;

import java.io.PrintWriter;
import java.time.LocalDateTime;

// bảng đơn ứng tuyển
public class Application extends BaseEntity{
    private Candidate candidateID;
    private Jobs jodID;
    private CV cvID;
    private LocalDateTime appliedAt;
    private String coverLetter;
    private String description;
    private int status;

    public Application() {
    }

    public Application(Candidate candidateID, Jobs jodID, CV cvID, LocalDateTime appliedAt, String coverLetter, String description, int status) {
        this.candidateID = candidateID;
        this.jodID = jodID;
        this.cvID = cvID;
        this.appliedAt = appliedAt;
        this.coverLetter = coverLetter;
        this.description = description;
        this.status = status;
    }

    public Application(int id, Candidate candidateID, Jobs jodID, CV cvID, LocalDateTime appliedAt, String coverLetter, String description, int status) {
        super(id);
        this.candidateID = candidateID;
        this.jodID = jodID;
        this.cvID = cvID;
        this.appliedAt = appliedAt;
        this.coverLetter = coverLetter;
        this.description = description;
        this.status = status;
    }

    public Candidate getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(Candidate candidateID) {
        this.candidateID = candidateID;
    }

    public Jobs getJodID() {
        return jodID;
    }

    public void setJodID(Jobs jodID) {
        this.jodID = jodID;
    }

    public CV getCvID() {
        return cvID;
    }

    public void setCvID(CV cvID) {
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
