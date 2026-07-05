package model;

public class Employers extends BaseEntity {

    private Users user;
    private Company company;
    private Role role;

    public Employers() {
    }

    public Employers(int id, Users user, Company company,Role role) {
        super(id);
        this.user = user;
        this.company = company;
        this.role = role;
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