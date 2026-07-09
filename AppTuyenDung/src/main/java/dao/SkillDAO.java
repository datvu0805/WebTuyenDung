package dao;

import config.DatabaseConfig;
import model.Skill;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO extends DatabaseConfig implements TDAO<Skill> {

    @Override
    public void add(Skill entity) {
        String sql = "INSERT INTO skills (skill_name, created_at, updated_at) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getSkillName());
            ps.setObject(2, LocalDateTime.now());
            ps.setObject(3, LocalDateTime.now());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) entity.setId(rs.getInt("id"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Skill entity) {
        String sql = "UPDATE skills SET skill_name = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getSkillName());
            ps.setInt(2, entity.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM skills WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Skill getById(int id) {
        String sql = "SELECT * FROM skills WHERE id = ?";
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
    public List<Skill> getAll() {
        String sql = "SELECT * FROM skills ORDER BY skill_name";
        List<Skill> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private Skill mapRow(ResultSet rs) throws SQLException {
        Skill s = new Skill();
        s.setId(rs.getInt("id"));
        s.setSkillName(rs.getString("skill_name"));
        return s;
    }
}
