package dao;

import config.DatabaseConfig;
import model.Candidates;
import model.Role;
import model.Users;

import java.sql.*;

public class CandicateDAO extends DatabaseConfig {

    public boolean add(int userId) {
        String sql = "INSERT INTO candidates(user_id) VALUES (?)";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Candidates mapCandidate(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("role_id"));
        role.setRoleName(rs.getString("role_name"));

        Users user = new Users(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("avatar_url"),
                rs.getString("email"),
                rs.getDate("date_of_birth") == null
                        ? null
                        : rs.getDate("date_of_birth").toLocalDate(),
                rs.getString("phone_number"),
                rs.getString("address"),
                role
        );
        user.setId(rs.getInt("user_id"));

        Candidates candidate = new Candidates(rs.getInt("candidate_id"));
        candidate.setUser(user);

        return candidate;
    }

    public Candidates findById(int id) {
        String sql = """
            SELECT
                c.id AS candidate_id,
                u.id AS user_id,
                u.username,
                u.password,
                u.full_name,
                u.avatar_url,
                u.email,
                u.date_of_birth,
                u.phone_number,
                u.address,
                r.id AS role_id,
                r.role_name
            FROM candidates c
            JOIN users u ON c.user_id = u.id
            JOIN roles r ON u.role_id = r.id
            WHERE c.id = ?
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCandidate(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Candidates findByUserId(int userId) {
        String sql = """
            SELECT
                c.id AS candidate_id,
                u.id AS user_id,
                u.username,
                u.password,
                u.full_name,
                u.avatar_url,
                u.email,
                u.date_of_birth,
                u.phone_number,
                u.address,
                r.id AS role_id,
                r.role_name
            FROM candidates c
            JOIN users u ON c.user_id = u.id
            JOIN roles r ON u.role_id = r.id
            WHERE u.id = ?
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCandidate(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM candidates WHERE id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}