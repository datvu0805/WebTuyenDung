package model;

import java.time.LocalDateTime;

public class FavoriteJob extends BaseEntity{
    private Candidates candidateID;
    private Job jobID;
    private LocalDateTime savedAt;

    public FavoriteJob(Candidates candidateID, Job jobID, LocalDateTime savedAt) {
        this.candidateID = candidateID;
        this.jobID = jobID;
        this.savedAt = savedAt;
    }

    public FavoriteJob(int id, Candidates candidateID, Job jobID, LocalDateTime savedAt) {
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

    public Job getJobID() {
        return jobID;
    }

    public void setJobID(Job jobID) {
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
