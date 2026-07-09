package model;

import constant.JobStatus;

import java.time.LocalDateTime;

public class Job extends BaseEntity{
    private Employers employerID;
    private String title;
    private String description;
    private Double minSalary;
    private Double maxSalary;
    private String currency;
    private String location;
    private String experience;
    private int quantity;
    private LocalDateTime postedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime applicationDeadline;
    private JobStatus status;
    private Boolean isHiddenOnExpiry;

    public Job(Employers employerID, String title, String description, Double minSalary, Double maxSalary, String currency, String location, String experience, int quantity, LocalDateTime postedAt, LocalDateTime expiredAt, LocalDateTime applicationDeadline, JobStatus status, Boolean isHiddenOnExpiry) {
        this.employerID = employerID;
        this.title = title;
        this.description = description;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.currency = currency;
        this.location = location;
        this.experience = experience;
        this.quantity = quantity;
        this.postedAt = postedAt;
        this.expiredAt = expiredAt;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.isHiddenOnExpiry = isHiddenOnExpiry;
    }

    public Job(int id, Employers employerID, String title, String description, Double minSalary, Double maxSalary, String currency, String location, String experience, int quantity, LocalDateTime postedAt, LocalDateTime expiredAt, LocalDateTime applicationDeadline, JobStatus status, Boolean isHiddenOnExpiry) {
        super(id);
        this.employerID = employerID;
        this.title = title;
        this.description = description;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.currency = currency;
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

    public Double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Double minSalary) {
        this.minSalary = minSalary;
    }

    public Double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
