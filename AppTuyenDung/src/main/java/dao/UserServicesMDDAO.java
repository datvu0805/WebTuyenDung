package dao;

import config.DatabaseConfig;
import model.ServicePackages;
import model.UserServicesMD;
import model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserServicesMDDAO extends DatabaseConfig {

    public void insertUserService(Connection conn, int userId, int packageId, LocalDate startDate, LocalDate endDate, int status) {
        String sql = """
            INSERT INTO user_services (user_id, package_id, start_date, end_date, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, packageId);
            ps.setTimestamp(3, Timestamp.valueOf(startDate.atStartOfDay()));
            ps.setTimestamp(4, Timestamp.valueOf(endDate.atStartOfDay()));
            ps.setInt(5, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kích hoạt gói VIP: " + e.getMessage(), e);
        }
    }

    /** Deactivate expired or previous active services for user (same audience package family optional). */
    public void deactivateActiveServices(Connection conn, int userId) {
        String sql = """
            UPDATE user_services
            SET status = 0, updated_at = CURRENT_TIMESTAMP
            WHERE user_id = ? AND status = 1
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi hủy gói VIP cũ: " + e.getMessage(), e);
        }
    }

    public UserServicesMD findActiveByUserId(int userId) {
        String sql = """
            SELECT us.id, us.user_id, us.package_id, us.start_date, us.end_date, us.status,
                   sp.package_name, sp.target_audience, sp.price, sp.duration_days, sp.benefit_type, sp.description
            FROM user_services us
            JOIN service_packages sp ON sp.id = us.package_id
            WHERE us.user_id = ? AND us.status = 1 AND us.end_date >= CURRENT_TIMESTAMP
            ORDER BY us.end_date DESC
            LIMIT 1
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra VIP: " + e.getMessage(), e);
        }
        return null;
    }

    public List<UserServicesMD> findHistoryByUserId(int userId) {
        String sql = """
            SELECT us.id, us.user_id, us.package_id, us.start_date, us.end_date, us.status,
                   sp.package_name, sp.target_audience, sp.price, sp.duration_days, sp.benefit_type, sp.description
            FROM user_services us
            JOIN service_packages sp ON sp.id = us.package_id
            WHERE us.user_id = ?
            ORDER BY us.created_at DESC
            """;
        List<UserServicesMD> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy lịch sử gói: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean isVipActive(int userId) {
        return findActiveByUserId(userId) != null;
    }

    private UserServicesMD map(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setId(rs.getInt("user_id"));

        ServicePackages pkg = new ServicePackages();
        pkg.setId(rs.getInt("package_id"));
        pkg.setPackageName(rs.getString("package_name"));
        pkg.setTargetAudience(rs.getString("target_audience"));
        pkg.setPrice(rs.getDouble("price"));
        pkg.setDurationDays(rs.getInt("duration_days"));
        pkg.setBenifitType(rs.getString("benefit_type"));
        pkg.setDescription(rs.getString("description"));

        UserServicesMD us = new UserServicesMD();
        us.setId(rs.getInt("id"));
        us.setUserID(user);
        us.setPackageID(pkg);

        Timestamp start = rs.getTimestamp("start_date");
        Timestamp end = rs.getTimestamp("end_date");
        if (start != null) {
            us.setStartDate(start.toLocalDateTime().toLocalDate());
        }
        if (end != null) {
            us.setEndDate(end.toLocalDateTime().toLocalDate());
        }
        us.setStatus(String.valueOf(rs.getInt("status")));
        return us;
    }
}
