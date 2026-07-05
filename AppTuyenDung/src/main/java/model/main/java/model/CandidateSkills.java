package model.main.java.model;

public class CandidateSkills extends BaseEntity {
    private Candidates candidateID;
    private Skills skillID;

    public CandidateSkills(Candidates candidateID, Skills skillID) {
        this.candidateID = candidateID;
        this.skillID = skillID;
    }

    public CandidateSkills(int id, Candidates candidateID, Skills skillID) {
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
