package dto;

import java.time.LocalDateTime;

public class JobDTO {

    private Integer employerId;
    private String title;
    private String description;
    private Double salary;
    private String location;
    private String experience;
    private Integer quantity;
    private LocalDateTime postedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime applicationDeadline;
    private Short status;
    private Boolean hiddenOnExpiry;

    public JobDTO() {
    }

    public JobDTO(Integer employerId, String title, String description, Double salary, String location, String experience, Integer quantity, LocalDateTime postedAt, LocalDateTime expiredAt, LocalDateTime applicationDeadline, Short status, Boolean hiddenOnExpiry) {
        this.employerId = employerId;
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
        this.hiddenOnExpiry = hiddenOnExpiry;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
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

    public Boolean getHiddenOnExpiry() {
        return hiddenOnExpiry;
    }

    public void setHiddenOnExpiry(Boolean hiddenOnExpiry) {
        this.hiddenOnExpiry = hiddenOnExpiry;
    }
}