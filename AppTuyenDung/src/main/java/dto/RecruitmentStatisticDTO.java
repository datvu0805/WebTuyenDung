package dto;

public class RecruitmentStatisticDTO {
    private int totalJobs;
    private int jobsWithApplicants;
    private int jobsWithHires;
    private int totalApplications;
    private int hiredApplications;
    private double jobApplicantRate;
    private double jobHireRate;
    private double applicationHireRate;

    public RecruitmentStatisticDTO() {
    }

    public RecruitmentStatisticDTO(int totalJobs, int jobsWithApplicants, int jobsWithHires,
                                   int totalApplications, int hiredApplications) {
        this.totalJobs = totalJobs;
        this.jobsWithApplicants = jobsWithApplicants;
        this.jobsWithHires = jobsWithHires;
        this.totalApplications = totalApplications;
        this.hiredApplications = hiredApplications;
        this.jobApplicantRate = percent(jobsWithApplicants, totalJobs);
        this.jobHireRate = percent(jobsWithHires, totalJobs);
        this.applicationHireRate = percent(hiredApplications, totalApplications);
    }

    private double percent(int numerator, int denominator) {
        return denominator == 0 ? 0 : numerator * 100.0 / denominator;
    }

    public int getTotalJobs() { return totalJobs; }
    public int getJobsWithApplicants() { return jobsWithApplicants; }
    public int getJobsWithHires() { return jobsWithHires; }
    public int getTotalApplications() { return totalApplications; }
    public int getHiredApplications() { return hiredApplications; }
    public double getJobApplicantRate() { return jobApplicantRate; }
    public double getJobHireRate() { return jobHireRate; }
    public double getApplicationHireRate() { return applicationHireRate; }
}
