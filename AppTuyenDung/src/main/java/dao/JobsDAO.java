package dao;

import config.DatabaseConfig;
import model.JobSkills;
import model.Jobs;
import model.Skills;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class JobsDAO extends DatabaseConfig implements TDAO<Jobs> {
    @Override
    public void add(Jobs jobs) {
        String sql = "INSERT INTO jobs (employer_id, title, description, salary, location, experience, quantity, posted_at, expired_at, application_deadline, status, is_hidden_on_expiry, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobs.getEmployerID().getId());
            ps.setString(2, jobs.getTitle());
            ps.setString(3, jobs.getDescription());
            ps.setDouble(4, jobs.getSalary());
            ps.setString(5, jobs.getLocation());
            ps.setInt(7, jobs.getQuantity());
            ps.setObject(8, jobs.getPostedAt());
            ps.setObject(9, jobs.getExpiredAt());
            ps.setObject(10, jobs.getApplicationDeadline());
            ps.setInt(11, jobs.getStatus());
            ps.setBoolean(12, jobs.getHiddenOnExpiry());
            ps.setObject(13, jobs.getCreatedAt());
            ps.setObject(14, jobs.getUpdatedAt());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // đồng bộ hoá id
                    jobs.setId(rs.getInt("id"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Jobs jobs) {
        String sql = " UPDATE jobs SET employer_id = ?, title = ?, description = ?, salary = ?, location = ?, experience = ?, quantity = ?, posted_at = ?, expired_at = ?, application_deadline = ?, status = ?, is_hidden_on_expiry = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? ";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobs.getEmployerID().getId());
            ps.setString(2, jobs.getTitle());
            ps.setString(3, jobs.getDescription());
            ps.setDouble(4, jobs.getSalary());
            ps.setString(5, jobs.getLocation());
            ps.setInt(7, jobs.getQuantity());
            ps.setObject(8, jobs.getPostedAt());
            ps.setObject(9, jobs.getExpiredAt());
            ps.setObject(10, jobs.getApplicationDeadline());
            ps.setInt(11, jobs.getStatus());
            ps.setBoolean(12, jobs.getHiddenOnExpiry());
            ps.setObject(13, jobs.getCreatedAt());
            ps.setObject(14, jobs.getUpdatedAt());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = " DELETE FROM jobs WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Jobs getById(int id) {
        String sql = "SELECT * FROM jobs WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.getInt("id");
                rs.getString("title");
                rs.getString("description");
                rs.getDouble("salary");
                rs.getString("location");
                rs.getInt("quantity");
                rs.getObject("posted_at");
                rs.getObject("expired_at");
                rs.getObject("application_deadline");
                rs.getInt("status");
                rs.getBoolean("is_hidden_on_expiry");
                rs.getObject("created_at");
                rs.getObject("updated_at");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Jobs> getAll() {
        String sql = "SELECT * FROM jobs";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery(sql)) {

            if (rs.next()) {
                rs.getInt("id");
                rs.getString("title");
                rs.getString("description");
                rs.getDouble("salary");
                rs.getString("location");
                rs.getInt("quantity");
                rs.getObject("posted_at");
                rs.getObject("expired_at");
                rs.getObject("application_deadline");
                rs.getInt("status");
                rs.getBoolean("is_hidden_on_expiry");
                rs.getObject("created_at");
                rs.getObject("updated_at");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }

    public void addSkill(Jobs jobsID, Skills skillsID) {

    }

    public void delete(Jobs jobsID, Skills skillsID) {

    }

    public List<JobSkills> getAll(Jobs jobsID, Skills skillsID) {
        return List.of();
    }
}
