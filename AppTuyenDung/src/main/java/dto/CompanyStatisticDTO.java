package dto;

public class CompanyStatisticDTO {
    private int companyId;
    private String companyName;
    private int totalJobs;
    private int jobsWithApplicants;
    private int jobsWithHires;
    private int totalApplications;
    private int hiredApplications;
    private double jobFillRate;
    private double applicationHireRate;

    public CompanyStatisticDTO() {
    }

    public CompanyStatisticDTO(int companyId, String companyName, int totalJobs, int jobsWithApplicants,
                               int jobsWithHires, int totalApplications, int hiredApplications) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.totalJobs = totalJobs;
        this.jobsWithApplicants = jobsWithApplicants;
        this.jobsWithHires = jobsWithHires;
        this.totalApplications = totalApplications;
        this.hiredApplications = hiredApplications;
        this.jobFillRate = totalJobs == 0 ? 0 : jobsWithHires * 100.0 / totalJobs;
        this.applicationHireRate = totalApplications == 0 ? 0 : hiredApplications * 100.0 / totalApplications;
    }

    public int getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public int getTotalJobs() { return totalJobs; }
    public int getJobsWithApplicants() { return jobsWithApplicants; }
    public int getJobsWithHires() { return jobsWithHires; }
    public int getTotalApplications() { return totalApplications; }
    public int getHiredApplications() { return hiredApplications; }
    public double getJobFillRate() { return jobFillRate; }
    public double getApplicationHireRate() { return applicationHireRate; }
}
