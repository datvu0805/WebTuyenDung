package dto;

import model.CV;
import model.Candidates;

import java.util.List;

public class CandidateProfileCVDTO {
    private Candidates candidateInfo;
    private List<CV> cvList;

    public CandidateProfileCVDTO() {
    }

    public CandidateProfileCVDTO(Candidates candidateInfo, List<CV> cvList) {
        this.candidateInfo = candidateInfo;
        this.cvList = cvList;
    }

    public Candidates getCandidateInfo() {
        return candidateInfo;
    }

    public void setCandidateInfo(Candidates candidateInfo) {
        this.candidateInfo = candidateInfo;
    }

    public List<CV> getCvList() {
        return cvList;
    }

    public void setCvList(List<CV> cvList) {
        this.cvList = cvList;
    }
}
