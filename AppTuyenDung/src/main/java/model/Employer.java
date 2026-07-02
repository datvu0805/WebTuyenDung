package model;

public class Employer extends BaseEntity {

    private User user;
    private Company company;

    public Employer() {
    }

    @Override
    public String getInfo() {
        return "";
    }

    public Employer(User user, Company company) {
        this.user = user;
        this.company = company;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}