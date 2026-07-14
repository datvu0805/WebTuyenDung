package dao;

import config.DatabaseConfig;
import model.SystemSetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SystemSettingDAO extends DatabaseConfig {

    public String getValue(String key) {

        String sql = "SELECT value FROM system_settings WHERE key = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<SystemSetting> getAll() {

        String sql = "SELECT key, value, updated_at FROM system_settings ORDER BY key";

        List<SystemSetting> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SystemSetting setting = new SystemSetting(rs.getString("key"), rs.getString("value"));
                setting.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
                list.add(setting);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // UPSERT: tạo mới nếu chưa có key, cập nhật nếu đã có
    public void setValue(String key, String value) {

        String sql = """
                INSERT INTO system_settings (key, value, updated_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value, updated_at = CURRENT_TIMESTAMP
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.setString(2, value);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
