package model;

// Bảng trình độ học vấn tối thiểu yêu cầu của công việc
public class JobEducation extends BaseEntity {

    private Job jobID;
    private EducationLevel educationLevelID;

    public JobEducation() {
    }

    public JobEducation(Job jobID, EducationLevel educationLevelID) {
        this.jobID = jobID;
        this.educationLevelID = educationLevelID;
    }

    public JobEducation(int id, Job jobID, EducationLevel educationLevelID) {
        super(id);
        this.jobID = jobID;
        this.educationLevelID = educationLevelID;
    }

    public Job getJobID() {
        return jobID;
    }

    public void setJobID(Job jobID) {
        this.jobID = jobID;
    }

    public EducationLevel getEducationLevelID() {
        return educationLevelID;
    }

    public void setEducationLevelID(EducationLevel educationLevelID) {
        this.educationLevelID = educationLevelID;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
