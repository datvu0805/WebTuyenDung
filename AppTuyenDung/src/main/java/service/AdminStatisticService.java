package service;

import dao.AdminStatisticDAO;
import dto.AdminStatisticDTO;
import dto.CompanyStatisticDTO;
import dto.RecruitmentStatisticDTO;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminStatisticService {
    private final AdminStatisticDAO statisticDAO = new AdminStatisticDAO();

    private static final Map<Integer, String> JOB_STATUS_LABELS = Map.of(
            1, "Đang tuyển",
            2, "Tạm dừng",
            3, "Đã hết hạn",
            4, "Đã đóng"
    );

    private static final Map<Integer, String> APP_STATUS_LABELS = Map.of(
            0, "Chờ xử lý",
            1, "Phỏng vấn",
            2, "Nhận việc",
            3, "Từ chối"
    );

    public AdminStatisticDTO getStatistic() {
        return getStatistic(null, null);
    }

    public AdminStatisticDTO getStatistic(LocalDate from, LocalDate to) {
        validateRange(from, to);
        AdminStatisticDTO dto = new AdminStatisticDTO();

        Map<String, Integer> byRole = statisticDAO.countUsersByRole(from, to);
        dto.setTotalUserByRole(byRole);
        dto.setTotalUsers(statisticDAO.countTable("users", from, to));
        dto.setTotalCandidates(byRole.getOrDefault("CANDIDATE", 0));
        dto.setTotalEmployers(byRole.getOrDefault("EMPLOYER", 0));
        dto.setTotalAdmins(byRole.getOrDefault("ADMIN", 0));

        dto.setTotalCompanies(statisticDAO.countTable("companies", from, to));
        dto.setTotalJobs(statisticDAO.countTable("jobs", from, to));
        dto.setTotalSkills(statisticDAO.countTable("skills", from, to));
        dto.setTotalApplications(statisticDAO.countTable("applications", from, to));
        dto.setTotalCvs(statisticDAO.countTable("cvs", from, to));
        dto.setTotalMessages(statisticDAO.countTable("messages", from, to));
        dto.setActiveVipUsers(statisticDAO.countActiveVip(from, to));

        dto.setJobsByStatus(toLabels(statisticDAO.countJobsByStatus(from, to), JOB_STATUS_LABELS));
        dto.setApplicationsByStatus(toLabels(statisticDAO.countApplicationsByStatus(from, to), APP_STATUS_LABELS));

        Map<Integer, Integer> txnStatus = statisticDAO.countTransactionsByStatus(from, to);
        dto.setPendingTransactions(txnStatus.getOrDefault(0, 0));
        dto.setSuccessfulTransactions(txnStatus.getOrDefault(1, 0));
        dto.setFailedTransactions(txnStatus.getOrDefault(2, 0));
        dto.setTotalRevenue(statisticDAO.sumSuccessfulRevenue(from, to));

        LocalDate trendTo = to == null ? LocalDate.now().plusDays(1) : to.plusDays(1);
        LocalDate trendFrom = from == null ? trendTo.minusMonths(6) : from.withDayOfMonth(1);
        dto.setMonthlyUsers(statisticDAO.monthlyCounts("users", "created_at", trendFrom, trendTo));
        dto.setMonthlyJobs(statisticDAO.monthlyCounts("jobs", "created_at", trendFrom, trendTo));
        dto.setMonthlyApplications(statisticDAO.monthlyCounts("applications", "applied_at", trendFrom, trendTo));
        dto.setMonthlyRevenue(statisticDAO.monthlyRevenue(trendFrom, trendTo));
        dto.setFromDate(from == null ? null : from.toString());
        dto.setToDate(to == null ? null : to.toString());
        return dto;
    }

    public List<CompanyStatisticDTO> getCompanyStatistics(LocalDate from, LocalDate to) {
        validateRange(from, to);
        return statisticDAO.companyStatistics(from, to);
    }

    public RecruitmentStatisticDTO getRecruitmentStatistics(LocalDate from, LocalDate to) {
        validateRange(from, to);
        int totalJobs = statisticDAO.countTable("jobs", from, to);
        int jobsWithApplicants = statisticDAO.countJobsWithApplicants(from, to);
        int jobsWithHires = statisticDAO.countJobsWithHires(from, to);
        int totalApplications = statisticDAO.countApplications(from, to);
        int hiredApplications = statisticDAO.countHiredApplications(from, to);
        return new RecruitmentStatisticDTO(totalJobs, jobsWithApplicants, jobsWithHires,
                totalApplications, hiredApplications);
    }
    private Map<String, Integer> toLabels(Map<Integer, Integer> source, Map<Integer, String> labels) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : source.entrySet()) {
            result.put(labels.getOrDefault(entry.getKey(), "Trạng thái " + entry.getKey()), entry.getValue());
        }
        return result;
    }


    public void validateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        if (to != null && to.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày kết thúc không được ở tương lai");
        }
    }
}
