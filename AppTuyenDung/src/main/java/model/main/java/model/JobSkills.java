package model.main.java.model;


// Bảng kỹ năng yêu cầu của công việc
public class JobSkills extends BaseEntity {
    private Jobs jobID;
    private Skills skillID;

    public JobSkills(Jobs jobID, Skills skillID) {
        this.jobID = jobID;
        this.skillID = skillID;
    }

    public JobSkills(int id, Jobs jobID, Skills skillID) {
        super(id);
        this.jobID = jobID;
        this.skillID = skillID;
    }

    public JobSkills() {

    }

    public Jobs getJobID() {
        return jobID;
    }

    public void setJobID(Jobs jobID) {
        this.jobID = jobID;
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
