package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class Candidates extends BaseEntity {

    private Users user;
    private Double desiredMinSalary;
    private Double desiredMaxSalary;

    public Candidates() {
    }

    public Candidates(int id) {
        super(id);
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

    public Double getDesiredMinSalary() {
        return desiredMinSalary;
    }

    public void setDesiredMinSalary(Double desiredMinSalary) {
        this.desiredMinSalary = desiredMinSalary;
    }

    public Double getDesiredMaxSalary() {
        return desiredMaxSalary;
    }

    public void setDesiredMaxSalary(Double desiredMaxSalary) {
        this.desiredMaxSalary = desiredMaxSalary;
    }
}