package model;

import java.time.LocalDateTime;

public class Job extends BaseEntity{
    private Employers employerID;
    private String title;
    private String description;
    private Double salary;
    private String location;
    private String experience;
    private int quantity;
    private LocalDateTime postedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime applicationDeadline;
    private Short status;
    private Boolean isHiddenOnExpiry;

    public Job(Employers employerID, String title, String description, Double salary, String location, String experience, int quantity, LocalDateTime postedAt, LocalDateTime expiredAt, LocalDateTime applicationDeadline, Short status, Boolean isHiddenOnExpiry) {
        this.employerID = employerID;
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.location = location;
        this.experience = experience;
        this.quantity = quantity;
        this.postedAt = postedAt;
        this.expiredAt = expiredAt;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.isHiddenOnExpiry = isHiddenOnExpiry;
    }

    public Job(int id, Employers employerID, String title, String description, Double salary, String location, String experience, int quantity, LocalDateTime postedAt, LocalDateTime expiredAt, LocalDateTime applicationDeadline, Short status, Boolean isHiddenOnExpiry) {
        super(id);
        this.employerID = employerID;
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.location = location;
        this.experience = experience;
        this.quantity = quantity;
        this.postedAt = postedAt;
        this.expiredAt = expiredAt;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.isHiddenOnExpiry = isHiddenOnExpiry;
    }

    public Job() {

    }
    public Job(int id) {
        super(id);
    }

    public Boolean getHiddenOnExpiry() {
        return isHiddenOnExpiry;
    }

    public void setHiddenOnExpiry(Boolean hiddenOnExpiry) {
        isHiddenOnExpiry = hiddenOnExpiry;
    }

    public Employers getEmployerID() {
        return employerID;
    }

    public void setEmployerID(Employers employerID) {
        this.employerID = employerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
