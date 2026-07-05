package model.main.java.dao;

import model.main.java.config.DatabaseConfig;
import model.main.java.model.Application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ApplicationDAO extends DatabaseConfig implements IDAO<Application> {

    @Override
    public void add(Application app) {
        String sql = "INSERT INTO applications(candidate_id, job_id, cv_id, applied_at, cover_letter, description, status, created_at, update_at" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, app.getCandidateID().getId());
            ps.setInt(2, app.getJodID().getId());
            ps.setInt(3, app.getCvID().getId());
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
    }

    @Override
    public void update(Application entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Application> getAll() {
        return List.of();
    }


}