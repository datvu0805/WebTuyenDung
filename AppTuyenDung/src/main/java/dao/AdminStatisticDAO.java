package dao;

import config.DatabaseConfig;
import dto.AdminStatisticDTO;
import dto.CompanyStatisticDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminStatisticDAO extends DatabaseConfig {

    public int countTable(String table) {
        return countTable(table, null, null);
    }

    public int countTable(String table, LocalDate from, LocalDate to) {
        requireSafeTable(table);
        String sql = "SELECT COUNT(*) FROM " + table + datePredicate("created_at", from, to);
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Count " + table + " failed: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> countUsersByRole() {
        return countUsersByRole(null, null);
    }

    public Map<String, Integer> countUsersByRole(LocalDate from, LocalDate to) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT r.role_name, COUNT(*) AS total
            FROM users u JOIN roles r ON u.role_id = r.id
            %s GROUP BY r.role_name ORDER BY r.role_name
            """.formatted(datePredicate("u.created_at", from, to));
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.put(rs.getString("role_name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Map<Integer, Integer> countJobsByStatus() {
        return countJobsByStatus(null, null);
    }

    public Map<Integer, Integer> countJobsByStatus(LocalDate from, LocalDate to) {
        return countByStatus("jobs", "created_at", from, to);
    }

    public Map<Integer, Integer> countApplicationsByStatus() {
        return countApplicationsByStatus(null, null);
    }

    public Map<Integer, Integer> countApplicationsByStatus(LocalDate from, LocalDate to) {
        return countByStatus("applications", "applied_at", from, to);
    }

    public int countActiveVip() {
        return countActiveVip(null, null);
    }

    public int countActiveVip(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT user_id) FROM user_services
            WHERE status = 1 AND end_date >= CURRENT_TIMESTAMP
            """);
        if (from != null) sql.append(" AND created_at >= ?");
        if (to != null) sql.append(" AND created_at < ?");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (from != null) ps.setObject(index++, from);
            if (to != null) ps.setObject(index, to.plusDays(1));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, Integer> countTransactionsByStatus() {
        return countTransactionsByStatus(null, null);
    }

    public Map<Integer, Integer> countTransactionsByStatus(LocalDate from, LocalDate to) {
        return countByStatus("transactions", "created_at", from, to);
    }

    public double sumSuccessfulRevenue() {
        return sumSuccessfulRevenue(null, null);
    }

    public double sumSuccessfulRevenue(LocalDate from, LocalDate to) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE status = 1"
                + datePredicate("created_at", from, to);
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AdminStatisticDTO.MonthlyCountDTO> monthlyCounts(String table, int months) {
        requireSafeTable(table);
        String dateColumn = "applications".equals(table) ? "applied_at" : "created_at";
        LocalDate to = LocalDate.now().plusDays(1);
        LocalDate from = LocalDate.now().withDayOfMonth(1).minusMonths(Math.max(months - 1, 0));
        return monthlyCounts(table, dateColumn, from, to);
    }

    public List<AdminStatisticDTO.MonthlyCountDTO> monthlyCounts(String table, String dateColumn, LocalDate from, LocalDate to) {
        requireSafeTable(table);
        requireSafeDateColumn(dateColumn);
        String sql = """
            SELECT to_char(date_trunc('month', %s), 'YYYY-MM') AS month,
                   COUNT(*)::int AS total
            FROM %s WHERE %s >= ? AND %s < ?
            GROUP BY 1 ORDER BY 1
            """.formatted(dateColumn, table, dateColumn, dateColumn);
        List<AdminStatisticDTO.MonthlyCountDTO> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, from); ps.setObject(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new AdminStatisticDTO.MonthlyCountDTO(rs.getString("month"), rs.getInt("total")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<AdminStatisticDTO.MonthlyCountDTO> monthlyRevenue(int months) {
        LocalDate to = LocalDate.now().plusDays(1);
        LocalDate from = LocalDate.now().withDayOfMonth(1).minusMonths(Math.max(months - 1, 0));
        return monthlyRevenue(from, to);
    }

    public List<AdminStatisticDTO.MonthlyCountDTO> monthlyRevenue(LocalDate from, LocalDate to) {
        String sql = """
            SELECT to_char(date_trunc('month', created_at), 'YYYY-MM') AS month,
                   COUNT(*)::int AS total, COALESCE(SUM(amount), 0) AS amount
            FROM transactions WHERE status = 1 AND created_at >= ? AND created_at < ?
            GROUP BY 1 ORDER BY 1
            """;
        List<AdminStatisticDTO.MonthlyCountDTO> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, from); ps.setObject(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new AdminStatisticDTO.MonthlyCountDTO(rs.getString("month"), rs.getInt("total"), rs.getDouble("amount")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<CompanyStatisticDTO> companyStatistics(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("""
            SELECT c.id, c.company_name,
                   COUNT(DISTINCT j.id)::int AS total_jobs,
                   COUNT(DISTINCT j.id) FILTER (WHERE a.id IS NOT NULL)::int AS jobs_with_applicants,
                   COUNT(DISTINCT j.id) FILTER (WHERE a.status = 2)::int AS jobs_with_hires,
                   COUNT(a.id)::int AS total_applications,
                   COUNT(a.id) FILTER (WHERE a.status = 2)::int AS hired_applications
            FROM companies c
            LEFT JOIN jobs j ON j.company_id = c.id
            LEFT JOIN applications a ON a.job_id = j.id
            """);
        appendDateFilter(sql, "j.created_at", from, to);
        sql.append(" GROUP BY c.id, c.company_name ORDER BY total_jobs DESC, c.company_name");
        List<CompanyStatisticDTO> result = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new CompanyStatisticDTO(rs.getInt("id"), rs.getString("company_name"),
                            rs.getInt("total_jobs"), rs.getInt("jobs_with_applicants"),
                            rs.getInt("jobs_with_hires"), rs.getInt("total_applications"),
                            rs.getInt("hired_applications")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải thống kê theo công ty", e);
        }
        return result;
    }

    public int countJobsWithApplicants(LocalDate from, LocalDate to) {
        return countDistinctJobs("a.id IS NOT NULL", from, to);
    }

    public int countJobsWithHires(LocalDate from, LocalDate to) {
        return countDistinctJobs("a.status = 2", from, to);
    }

    public int countApplications(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM applications");
        appendDateFilter(sql, "applied_at", from, to);
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countHiredApplications(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM applications WHERE status = 2");
        if (from != null) sql.append(" AND applied_at >= ?");
        if (to != null) sql.append(" AND applied_at < ?");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int countDistinctJobs(String condition, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT j.id) FROM jobs j LEFT JOIN applications a ON a.job_id = j.id");
        appendDateFilter(sql, "j.created_at", from, to);
        sql.append(sql.indexOf(" WHERE ") >= 0 ? " AND " : " WHERE ").append(condition);
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendDateFilter(StringBuilder sql, String column, LocalDate from, LocalDate to) {
        if (from != null) sql.append(" WHERE ").append(column).append(" >= ?");
        if (to != null) sql.append(from == null ? " WHERE " : " AND ").append(column).append(" < ?");
    }
    private Map<Integer, Integer> countByStatus(String table, String dateColumn, LocalDate from, LocalDate to) {
        Map<Integer, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) AS total FROM " + table + datePredicate(dateColumn, from, to) + " GROUP BY status ORDER BY status";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindDates(ps, 1, from, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.put(rs.getInt("status"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    private String datePredicate(String column, LocalDate from, LocalDate to) {
        if (from == null && to == null) return "";
        return " WHERE " + (from == null ? "" : column + " >= ?")
                + (from != null && to != null ? " AND " : "")
                + (to == null ? "" : column + " < ?");
    }

    private void bindDates(PreparedStatement ps, int index, LocalDate from, LocalDate to) throws SQLException {
        if (from != null) ps.setObject(index++, from);
        if (to != null) ps.setObject(index, to.plusDays(1));
    }

    private void requireSafeTable(String table) {
        if (!isSafeTable(table)) throw new IllegalArgumentException("Invalid table: " + table);
    }

    private void requireSafeDateColumn(String dateColumn) {
        if (!"created_at".equals(dateColumn) && !"applied_at".equals(dateColumn)) {
            throw new IllegalArgumentException("Invalid date column: " + dateColumn);
        }
    }

    private boolean isSafeTable(String table) {
        return switch (table) {
            case "users", "candidates", "employers", "companies", "jobs", "applications",
                 "skills", "cvs", "messages", "transactions", "user_services", "service_packages" -> true;
            default -> false;
        };
    }
}
