package model;

public class Employers extends BaseEntity {

    private Users user;
    private Company company;
    private Role role;

    public Employers() {
    }

<<<<<<< HEAD
    public Employers(int id, Users user, Company company,Role role) {
        super(id);
        this.user = user;
        this.company = company;
        this.role = role;
    }

=======
>>>>>>> 529be65e22bbfe6ee49ee151d50a67b157d00623
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