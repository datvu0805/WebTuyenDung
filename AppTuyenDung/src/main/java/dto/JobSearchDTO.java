package dto;

public class JobSearchDTO {

    private String title;
    private String location;
    private Double minSalary;
    private Double maxSalary;
    private String experience;
    private Short status;
    private Integer companyId;
    private int page = 1;
    private int size = 20;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JobSearchDTO() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public JobSearchDTO(String title, String location, Double minSalary, Double maxSalary, String experience, Short status, int page, int size) {
        this.title = title;
        this.location = location;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.experience = experience;
        this.status = status;
        this.page = page;
        this.size = size;
    }
}
