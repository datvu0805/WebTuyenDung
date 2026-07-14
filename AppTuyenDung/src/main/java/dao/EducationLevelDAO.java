package dao;

import config.DatabaseConfig;
import model.EducationLevel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EducationLevelDAO extends DatabaseConfig implements TDAO<EducationLevel> {

    @Override
    public void add(EducationLevel entity) {
        String sql = "INSERT INTO education_levels (level_name, created_at, updated_at) VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getLevelName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) entity.setId(rs.getInt("id"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(EducationLevel entity) {
        String sql = "UPDATE education_levels SET level_name = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getLevelName());
            ps.setInt(2, entity.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM education_levels WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EducationLevel getById(int id) {
        String sql = "SELECT * FROM education_levels WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<EducationLevel> getAll() {
        String sql = "SELECT * FROM education_levels ORDER BY id";
        List<EducationLevel> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private EducationLevel mapRow(ResultSet rs) throws SQLException {
        EducationLevel e = new EducationLevel();
        e.setId(rs.getInt("id"));
        e.setLevelName(rs.getString("level_name"));
        return e;
    }
}
