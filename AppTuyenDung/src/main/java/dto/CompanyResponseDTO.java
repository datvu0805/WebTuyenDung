package dto;

public class CompanyResponseDTO {

    private int id;
    private String companyName;
    private String description;
    private String createdAt;
    private String updatedAt;

    public CompanyResponseDTO() {
    }

    public CompanyResponseDTO(int id,
                              String companyName,
                              String description,
                              String createdAt,
                              String updatedAt) {
        this.id = id;
        this.companyName = companyName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}