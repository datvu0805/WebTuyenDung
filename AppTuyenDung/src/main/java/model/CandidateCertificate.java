package model;

import java.time.LocalDate;


// Bảng chứng chỉ của ứng viên
public class CandidateCertificate extends BaseEntity{
    private Candidates candidateID;
    private Certificate certificatesID;
    private String score;
// Ngày cấp chứng chỉ
    private LocalDate issueDate;
// Ngày hết hạn chứng chỉ
    private LocalDate expiryDate;
// Mô tả thêm
    private String description;

    public CandidateCertificate(Candidates candidateID, Certificate certificatesID, String score, LocalDate issueDate, LocalDate expiryDate, String description) {
        this.candidateID = candidateID;
        this.certificatesID = certificatesID;
        this.score = score;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public CandidateCertificate(int id, Candidates candidateID, Certificate certificatesID, String score, LocalDate issueDate, LocalDate expiryDate, String description) {
        super(id);
        this.candidateID = candidateID;
        this.certificatesID = certificatesID;
        this.score = score;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public Candidates getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(Candidates candidateID) {
        this.candidateID = candidateID;
    }

    public Certificate getCertificatesID() {
        return certificatesID;
    }

    public void setCertificatesID(Certificate certificatesID) {
        this.certificatesID = certificatesID;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
