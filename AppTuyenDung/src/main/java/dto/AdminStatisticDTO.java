package dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminStatisticDTO {
    private int totalUsers;
    private int totalCandidates;
    private int totalEmployers;
    private int totalAdmins;
    private int totalCompanies;
    private int totalSkills;
    private int totalJobs;
    private int totalApplications;
    private int totalCvs;
    private int totalMessages;
    private int activeVipUsers;
    private int successfulTransactions;
    private int pendingTransactions;
    private int failedTransactions;
    private double totalRevenue;
    private String fromDate;
    private String toDate;

    private Map<String, Integer> totalUserByRole = new LinkedHashMap<>();
    private Map<String, Integer> jobsByStatus = new LinkedHashMap<>();
    private Map<String, Integer> applicationsByStatus = new LinkedHashMap<>();

    private List<MonthlyCountDTO> monthlyUsers = new ArrayList<>();
    private List<MonthlyCountDTO> monthlyJobs = new ArrayList<>();
    private List<MonthlyCountDTO> monthlyApplications = new ArrayList<>();
    private List<MonthlyCountDTO> monthlyRevenue = new ArrayList<>();

    public static class MonthlyCountDTO {
        private String month;
        private int count;
        private double amount;

        public MonthlyCountDTO() {
        }

        public MonthlyCountDTO(String month, int count) {
            this.month = month;
            this.count = count;
        }

        public MonthlyCountDTO(String month, int count, double amount) {
            this.month = month;
            this.count = count;
            this.amount = amount;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalCandidates() {
        return totalCandidates;
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

    public int getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(int totalAdmins) {
        this.totalAdmins = totalAdmins;
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

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getTotalCvs() {
        return totalCvs;
    }

    public void setTotalCvs(int totalCvs) {
        this.totalCvs = totalCvs;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }

    public int getActiveVipUsers() {
        return activeVipUsers;
    }

    public void setActiveVipUsers(int activeVipUsers) {
        this.activeVipUsers = activeVipUsers;
    }

    public int getSuccessfulTransactions() {
        return successfulTransactions;
    }

    public void setSuccessfulTransactions(int successfulTransactions) {
        this.successfulTransactions = successfulTransactions;
    }

    public int getPendingTransactions() {
        return pendingTransactions;
    }

    public void setPendingTransactions(int pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public int getFailedTransactions() {
        return failedTransactions;
    }

    public void setFailedTransactions(int failedTransactions) {
        this.failedTransactions = failedTransactions;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Map<String, Integer> getTotalUserByRole() {
        return totalUserByRole;
    }

    public void setTotalUserByRole(Map<String, Integer> totalUserByRole) {
        this.totalUserByRole = totalUserByRole;
    }

    public Map<String, Integer> getJobsByStatus() {
        return jobsByStatus;
    }

    public void setJobsByStatus(Map<String, Integer> jobsByStatus) {
        this.jobsByStatus = jobsByStatus;
    }

    public Map<String, Integer> getApplicationsByStatus() {
        return applicationsByStatus;
    }

    public void setApplicationsByStatus(Map<String, Integer> applicationsByStatus) {
        this.applicationsByStatus = applicationsByStatus;
    }

    public List<MonthlyCountDTO> getMonthlyUsers() {
        return monthlyUsers;
    }

    public void setMonthlyUsers(List<MonthlyCountDTO> monthlyUsers) {
        this.monthlyUsers = monthlyUsers;
    }

    public List<MonthlyCountDTO> getMonthlyJobs() {
        return monthlyJobs;
    }

    public void setMonthlyJobs(List<MonthlyCountDTO> monthlyJobs) {
        this.monthlyJobs = monthlyJobs;
    }

    public List<MonthlyCountDTO> getMonthlyApplications() {
        return monthlyApplications;
    }

    public void setMonthlyApplications(List<MonthlyCountDTO> monthlyApplications) {
        this.monthlyApplications = monthlyApplications;
    }

    public List<MonthlyCountDTO> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(List<MonthlyCountDTO> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }
}
