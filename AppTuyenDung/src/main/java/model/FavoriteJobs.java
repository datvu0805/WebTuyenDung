package model;

import java.time.LocalDateTime;

public class FavoriteJobs extends BaseEntity{
    private int candidateID;
    private Jobs jobID;
    private LocalDateTime savedAt;

    public FavoriteJobs(int candidateID, Jobs jobID, LocalDateTime savedAt) {
        this.candidateID = candidateID;
        this.jobID = jobID;
        this.savedAt = savedAt;
    }

    public FavoriteJobs(int id, int candidateID, Jobs jobID, LocalDateTime savedAt) {
        super(id);
        this.candidateID = candidateID;
        this.jobID = jobID;
        this.savedAt = savedAt;
    }

    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
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
