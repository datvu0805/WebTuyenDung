package dao;

import config.DatabaseConfig;
import model.ServicePackages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicePackagesDAO extends DatabaseConfig {
    public ServicePackages getPackageById(Connection conn, int packageId){
        String sql = "SECLECT id, package_name, target_audience, price, duration_days, benefit_type, description FROM service_packages WHERE id = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, packageId);

            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    ServicePackages pkg = new ServicePackages();
                    pkg.setId(packageId);
                    pkg.setPackageName(rs.getString("package_name"));
                    pkg.setTargetAudience(rs.getString("target_audience"));
                    pkg.setPrice(rs.getDouble("price"));
                    pkg.setDurationDays(rs.getInt("duration_days"));
                    pkg.setBenifitType(rs.getString("benefit_type"));
                    pkg.setDescription(rs.getString("description"));
                    return pkg;

                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
