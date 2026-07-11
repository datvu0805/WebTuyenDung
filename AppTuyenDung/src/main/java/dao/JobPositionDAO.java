package dao;

import config.DatabaseConfig;
import model.JobPosition;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobPositionDAO extends DatabaseConfig {

    private JobPosition map(ResultSet rs) throws SQLException {
        JobPosition p = new JobPosition();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        p.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return p;
    }

    public List<JobPosition> getAll() {
        List<JobPosition> list = new ArrayList<>();
        String sql = "SELECT * FROM job_positions ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public JobPosition getById(int id) {
        String sql = "SELECT * FROM job_positions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public JobPosition add(JobPosition p) {
        String sql = "INSERT INTO job_positions (name, description) VALUES (?, ?) RETURNING *";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public boolean update(JobPosition p) {
        String sql = "UPDATE job_positions SET name=?, description=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM job_positions WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
