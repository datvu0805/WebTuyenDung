package dao;

import config.DatabaseConfig;
import model.ServicePackages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicePackagesDAO extends DatabaseConfig {

    private ServicePackages map(ResultSet rs) throws SQLException {
        ServicePackages pkg = new ServicePackages();
        pkg.setId(rs.getInt("id"));
        pkg.setPackageName(rs.getString("package_name"));
        pkg.setTargetAudience(rs.getString("target_audience"));
        pkg.setPrice(rs.getDouble("price"));
        pkg.setDurationDays(rs.getInt("duration_days"));
        pkg.setBenifitType(rs.getString("benefit_type"));
        pkg.setDescription(rs.getString("description"));
        return pkg;
    }

    public ServicePackages getPackageById(Connection conn, int packageId) {
        String sql = """
            SELECT id, package_name, target_audience, price, duration_days, benefit_type, description
            FROM service_packages
            WHERE id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy gói dịch vụ: " + e.getMessage(), e);
        }
        return null;
    }

    public ServicePackages getPackageById(int packageId) {
        try (Connection conn = getConnection()) {
            return getPackageById(conn, packageId);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy gói dịch vụ: " + e.getMessage(), e);
        }
    }

    public List<ServicePackages> getAll() {
        String sql = """
            SELECT id, package_name, target_audience, price, duration_days, benefit_type, description
            FROM service_packages
            ORDER BY price ASC
            """;
        List<ServicePackages> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách gói: " + e.getMessage(), e);
        }
        return list;
    }

    public List<ServicePackages> getByAudience(String targetAudience) {
        String sql = """
            SELECT id, package_name, target_audience, price, duration_days, benefit_type, description
            FROM service_packages
            WHERE UPPER(target_audience) = UPPER(?)
            ORDER BY price ASC
            """;
        List<ServicePackages> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, targetAudience);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy gói theo đối tượng: " + e.getMessage(), e);
        }
        return list;
    }
}
