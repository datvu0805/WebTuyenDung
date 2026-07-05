package dao;

import config.DatabaseConfig;
import model.Candidates;
import model.Role;
import model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CandicateDAO extends DatabaseConfig {
    public boolean add(int userId) {
        String sql = "INSERT INTO candidates(user_id) VALUES (?)";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void findById(int id) {
        String sql = "SELECT  c.id AS candidate_id,\n" +
                "\n" +
                "                    u.id AS user_id,\n" +
                "                    u.username,\n" +
                "                    u.password,\n" +
                "                    u.full_name,\n" +
                "                    u.avatar_url,\n" +
                "                    u.email,\n" +
                "                    u.date_of_birth,\n" +
                "                    u.phone_number,\n" +
                "                    u.address,\n" +
                "\n" +
                "                    r.id AS role_id,\n" +
                "                    r.name AS role_name" +
                "     FROM candidates c " +
                "     join users u on c.user_id = u.id " +
                "     join roles r on u.role_id = r.id      " +
                "     WHERE c.id = ?";
        try  (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Candidates candidate = new Candidates(
                            rs.getInt("id")
                    );
                    Users user = new Users(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("avatar_url"),
                            rs.getString("email"),
                            rs.getDate("date_of_birth").toLocalDate(),
                            rs.getString("phone_number"),
                            rs.getString("address")
                    );
                    Role role = new Role(
                            rs.getInt("id"),
                            rs.getString("role_name")
                    );
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void findByUserId(int userId) {
        String sql = "SELECT  c.id AS candidate_id,\n" +
                "\n" +
                "                    u.id AS user_id,\n" +
                "                    u.username,\n" +
                "                    u.password,\n" +
                "                    u.full_name,\n" +
                "                    u.avatar_url,\n" +
                "                    u.email,\n" +
                "                    u.date_of_birth,\n" +
                "                    u.phone_number,\n" +
                "                    u.address,\n" +
                "\n" +
                "                    r.id AS role_id,\n" +
                "                    r.name AS role_name" +
                "     FROM candidates c " +
                "     join users u on c.user_id = u.id " +
                "     join roles r on u.role_id = r.id      " +
                "     WHERE u.id = ?";
        try  (Connection conn = getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Candidates candidate = new Candidates(
                            rs.getInt("id")
                    );
                    Users user = new Users(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("avatar_url"),
                            rs.getString("email"),
                            rs.getDate("date_of_birth").toLocalDate(),
                            rs.getString("phone_number"),
                            rs.getString("address")
                    );
                    Role role = new Role(
                            rs.getInt("id"),
                            rs.getString("role_name")
                    );
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public boolean deleteById(int id) {
        String sql = "DELETE FROM candidates WHERE id = ?";
        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
                ){
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }
}
