package model;

import java.time.LocalDateTime;

public class FavoriteJobs extends BaseEntity{
    private Candidates candidateID;
    private Jobs jobID;
    private LocalDateTime savedAt;

    public FavoriteJobs(Candidates candidateID, Jobs jobID, LocalDateTime savedAt) {
        this.candidateID = candidateID;
        this.jobID = jobID;
        this.savedAt = savedAt;
    }

    public FavoriteJobs(int id, Candidates candidateID, Jobs jobID, LocalDateTime savedAt) {
        super(id);
        this.candidateID = candidateID;
        this.jobID = jobID;
        this.savedAt = savedAt;
    }

    public Candidates getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(Candidates candidateID) {
        this.candidateID = candidateID;
    }

    public Jobs getJobID() {
        return jobID;
    }

    public void setJobID(Jobs jobID) {
        this.jobID = jobID;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
