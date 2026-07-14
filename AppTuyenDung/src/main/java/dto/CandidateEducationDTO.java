package dto;

import java.time.LocalDate;

public class CandidateEducationDTO {

    private int id;
    private int candidateId;
    private int educationLevelId;
    private String schoolName;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private String gpa;
    private String description;

    public CandidateEducationDTO() {
    }

    public CandidateEducationDTO(int id, int candidateId, int educationLevelId, String schoolName, String major,
                                  LocalDate startDate, LocalDate endDate, String gpa, String description) {
        this.id = id;
        this.candidateId = candidateId;
        this.educationLevelId = educationLevelId;
        this.schoolName = schoolName;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gpa = gpa;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getEducationLevelId() {
        return educationLevelId;
    }

    public void setEducationLevelId(int educationLevelId) {
        this.educationLevelId = educationLevelId;
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
}
