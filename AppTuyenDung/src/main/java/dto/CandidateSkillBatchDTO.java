package dto;

import java.util.List;

public class CandidateSkillBatchDTO {

    private Integer candidateId;
    private List<Integer> skillIds;

    public CandidateSkillBatchDTO() {
    }

    public CandidateSkillBatchDTO(Integer candidateId, List<Integer> skillIds) {
        this.candidateId = candidateId;
        this.skillIds = skillIds;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public List<Integer> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Integer> skillIds) {
        this.skillIds = skillIds;
    }
}