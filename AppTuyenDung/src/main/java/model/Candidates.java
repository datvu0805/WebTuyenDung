package model;

public class Candidates extends BaseEntity {

    private Users user;

    public Candidates() {
    }

    @Override
    public String getInfo() {
        return "";
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}