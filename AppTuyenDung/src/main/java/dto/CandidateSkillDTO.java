package dto;

public class CandidateSkillDTO {

    private Integer candidateId;
    private Integer skillId;

    public CandidateSkillDTO() {
    }

    public CandidateSkillDTO(Integer candidateId, Integer skillId) {
        this.candidateId = candidateId;
        this.skillId = skillId;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }
}