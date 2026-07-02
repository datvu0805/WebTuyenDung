package model;

public class Candidate extends BaseEntity {

    private User user;

    public Candidate() {
    }

    @Override
    public String getInfo() {
        return "";
    }

    public Candidate(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}