package dto;

import java.time.LocalDate;

public class CandidateCertificateDTO {

    private int id;
    private int candidateId;
    private int certificateId;
    private String score;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String description;

    public CandidateCertificateDTO() {
    }

    public CandidateCertificateDTO(int id, int candidateId, int certificateId, String score, LocalDate issueDate, LocalDate expiryDate, String description) {
        this.id = id;
        this.candidateId = candidateId;
        this.certificateId = certificateId;
        this.score = score;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public int getCertificateId() {
        return certificateId;
    }

    public String getScore() {
        return score;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public void setCertificateId(int certificateId) {
        this.certificateId = certificateId;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}