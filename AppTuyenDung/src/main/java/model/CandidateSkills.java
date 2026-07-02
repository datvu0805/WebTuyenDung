package model;

public class CandidateSkills extends BaseEntity{
    private int candidateID;
    private Skills skillID;

    public CandidateSkills(int candidateID, Skills skillID) {
        this.candidateID = candidateID;
        this.skillID = skillID;
    }

    public CandidateSkills(int id, int candidateID, Skills skillID) {
        super(id);
        this.candidateID = candidateID;
        this.skillID = skillID;
    }

    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
        this.candidateID = candidateID;
    }

    public Skills getSkillID() {
        return skillID;
    }

    public void setSkillID(Skills skillID) {
        this.skillID = skillID;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
