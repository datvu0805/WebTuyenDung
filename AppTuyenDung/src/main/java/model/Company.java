package model;

public class Company extends BaseEntity {

    private String companyName;
    private String description;

    public Company() {
    }

    @Override
    public String getInfo() {
        return "";
    }

    public Company(int id, String companyName) {
        super(id);
        this.companyName = companyName;
    }

    public Company(String companyName, String description) {
        this.companyName = companyName;
        this.description = description;
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
}