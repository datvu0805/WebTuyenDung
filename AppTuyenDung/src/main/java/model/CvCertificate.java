package model;

// Bảng liên kết: chứng chỉ nào (trong số ứng viên đã khai) được gắn vào 1 CV cụ thể
public class CvCertificate extends BaseEntity {

    private CV cvID;
    private CandidateCertificate candidateCertificateID;

    public CvCertificate() {
    }

    public CvCertificate(CV cvID, CandidateCertificate candidateCertificateID) {
        this.cvID = cvID;
        this.candidateCertificateID = candidateCertificateID;
    }

    public CvCertificate(int id, CV cvID, CandidateCertificate candidateCertificateID) {
        super(id);
        this.cvID = cvID;
        this.candidateCertificateID = candidateCertificateID;
    }

    public CV getCvID() {
        return cvID;
    }

    public void setCvID(CV cvID) {
        this.cvID = cvID;
    }

    public CandidateCertificate getCandidateCertificateID() {
        return candidateCertificateID;
    }

    public void setCandidateCertificateID(CandidateCertificate candidateCertificateID) {
        this.candidateCertificateID = candidateCertificateID;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
