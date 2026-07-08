package dao;

import config.DatabaseConfig;
import model.UserServicesMD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// dịch vụ người dùng
public class UserServicesMDDAO extends DatabaseConfig {
    public void insertUserService(Connection conn, UserServicesMD us){
        String sql = "INSERT INTO user_services (user_id, package_id, start_date, end_date, status, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, us.getUserID().getId());
            ps.setInt(2, us.getPackageID().getId());
            ps.setObject(3, us.getStartDate()); // Đẩy dữ liệu cấu trúc LocalDate vào database
            ps.setObject(4, us.getEndDate());
            ps.setString(5, us.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
