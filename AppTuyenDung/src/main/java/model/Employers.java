package model;

public class Employers extends BaseEntity {

    private Users user;
    private Company company;

    public Employers() {
    }

    public Employers(int id) {
        super(id);
    }

    @Override
    public String getInfo() {
        return "";
    }

    public Employers(Users user, Company company) {
        this.user = user;
        this.company = company;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}