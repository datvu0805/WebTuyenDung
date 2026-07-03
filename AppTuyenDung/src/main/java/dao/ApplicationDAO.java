package dao;

import config.DatabaseConfig;
import model.Application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ApplicationDAO extends DatabaseConfig {
    public boolean insertApplication(Application app) {
        String sql = "INSERT INTO applications(candidate_id, job_id, cv_id, applied_at, cover_letter, description, status, created_at, update_at" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, app.getCandidateID());
            ps.setInt(2, app.getJodID());
            ps.setInt(3, app.getCvID());
            ps.setObject(4, app.getAppliedAt());
            ps.setString(5, app.getCoverLetter());
            ps.setString(6, app.getDescription());
            ps.setInt(7, app.getStatus());
            ps.setObject(8, app.getCreatedAt());
            ps.setObject(9, app.getUpdatedAt());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    }