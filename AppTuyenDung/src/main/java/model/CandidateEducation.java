package model;

import java.time.LocalDate;

// Bảng học vấn của ứng viên
public class CandidateEducation extends BaseEntity {

    private Candidates candidateID;
    private EducationLevel educationLevelID;
    private String schoolName;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private String gpa;
    private String description;

    public CandidateEducation() {
    }

    public CandidateEducation(int id) {
        super(id);
    }

    public CandidateEducation(Candidates candidateID, EducationLevel educationLevelID, String schoolName,
                               String major, LocalDate startDate, LocalDate endDate, String gpa, String description) {
        this.candidateID = candidateID;
        this.educationLevelID = educationLevelID;
        this.schoolName = schoolName;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gpa = gpa;
        this.description = description;
    }

    public CandidateEducation(int id, Candidates candidateID, EducationLevel educationLevelID, String schoolName,
                               String major, LocalDate startDate, LocalDate endDate, String gpa, String description) {
        super(id);
        this.candidateID = candidateID;
        this.educationLevelID = educationLevelID;
        this.schoolName = schoolName;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gpa = gpa;
        this.description = description;
    }

    public Candidates getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(Candidates candidateID) {
        this.candidateID = candidateID;
    }

    public EducationLevel getEducationLevelID() {
        return educationLevelID;
    }

    public void setEducationLevelID(EducationLevel educationLevelID) {
        this.educationLevelID = educationLevelID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
