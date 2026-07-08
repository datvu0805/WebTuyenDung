package model;

public class CandidateSkill extends BaseEntity{
    private Candidates candidateID;
    private Skill skillID;

    public CandidateSkill(Candidates candidateID, Skill skillID) {
        this.candidateID = candidateID;
        this.skillID = skillID;
    }

    public CandidateSkill(int id, Candidates candidateID, Skill skillID) {
        super(id);
        this.candidateID = candidateID;
        this.skillID = skillID;
    }

    public Candidates getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(Candidates candidateID) {
        this.candidateID = candidateID;
    }

    public Skill getSkillID() {
        return skillID;
    }

    public void setSkillID(Skill skillID) {
        this.skillID = skillID;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
