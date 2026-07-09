package model;



// Bảng kỹ năng yêu cầu của công việc
public class JobSkill extends BaseEntity{
    private Job jobID;
    private Skill skillID;

    public JobSkill(Job jobID, Skill skillID) {
        this.jobID = jobID;
        this.skillID = skillID;
    }

    public JobSkill(int id, Job jobID, Skill skillID) {
        super(id);
        this.jobID = jobID;
        this.skillID = skillID;
    }

    public JobSkill() {

    }

    public Job getJobID() {
        return jobID;
    }

    public void setJobID(Job jobID) {
        this.jobID = jobID;
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
