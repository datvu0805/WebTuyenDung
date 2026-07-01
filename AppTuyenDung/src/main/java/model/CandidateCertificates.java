package model;

import java.time.LocalDate;


// Bảng chứng chỉ của ứng viên
public class CandidateCertificates extends BaseEntity{
    private int candidateID;
    private Certificates certificatesID;
    private String score;
// Ngày cấp chứng chỉ
    private LocalDate issueDate;
// Ngày hết hạn chứng chỉ
    private LocalDate expiryDate;
// Mô tả thêm
    private String description;

    public CandidateCertificates(int candidateID, Certificates certificatesID, String score, LocalDate issueDate, LocalDate expiryDate, String description) {
        this.candidateID = candidateID;
        this.certificatesID = certificatesID;
        this.score = score;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public CandidateCertificates(int id, int candidateID, Certificates certificatesID, String score, LocalDate issueDate, LocalDate expiryDate, String description) {
        super(id);
        this.candidateID = candidateID;
        this.certificatesID = certificatesID;
        this.score = score;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
        this.candidateID = candidateID;
    }

    public Certificates getCertificatesID() {
        return certificatesID;
    }

    public void setCertificatesID(Certificates certificatesID) {
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
