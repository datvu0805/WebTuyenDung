package dto;

import java.util.HashMap;
import java.util.Map;

public class AdminStatisticDTO {
    private int totalUsers;
    private int totalCandidates;
    private int totalEmployers;
    private int totalCompanies;
    private int totalSkills;
    private int totalJobs;
    private Map<String ,Integer> totalUserByRole;
    public int getTotalUsers() {
        return totalUsers;
    }


    public Map<String, Integer> getTotalUserByRole() {
        return totalUserByRole;
    }

    public int getTotalCandidates() {
        return totalCandidates;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setTotalUserByRole(Map<String, Integer> totalUserByRole) {
        this.totalUserByRole = totalUserByRole;
    }

    public void setTotalCandidates(int totalCandidates) {
        this.totalCandidates = totalCandidates;
    }

    public int getTotalEmployers() {
        return totalEmployers;
    }

    public void setTotalEmployers(int totalEmployers) {
        this.totalEmployers = totalEmployers;
    }

    public int getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(int totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public int getTotalSkills() {
        return totalSkills;
    }

    public void setTotalSkills(int totalSkills) {
        this.totalSkills = totalSkills;
    }

    public int getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(int totalJobs) {
        this.totalJobs = totalJobs;
    }
}
