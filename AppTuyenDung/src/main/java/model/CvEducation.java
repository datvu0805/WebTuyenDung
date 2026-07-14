package model;

// Bảng liên kết: học vấn nào (trong số ứng viên đã khai) được gắn vào 1 CV cụ thể
public class CvEducation extends BaseEntity {

    private CV cvID;
    private CandidateEducation candidateEducationID;

    public CvEducation() {
    }

    public CvEducation(CV cvID, CandidateEducation candidateEducationID) {
        this.cvID = cvID;
        this.candidateEducationID = candidateEducationID;
    }

    public CvEducation(int id, CV cvID, CandidateEducation candidateEducationID) {
        super(id);
        this.cvID = cvID;
        this.candidateEducationID = candidateEducationID;
    }

    public CV getCvID() {
        return cvID;
    }

    public void setCvID(CV cvID) {
        this.cvID = cvID;
    }

    public CandidateEducation getCandidateEducationID() {
        return candidateEducationID;
    }

    public void setCandidateEducationID(CandidateEducation candidateEducationID) {
        this.candidateEducationID = candidateEducationID;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
