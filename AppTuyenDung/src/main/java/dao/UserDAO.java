package dao;

import config.DatabaseConfig;
import model.Role;
import model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DatabaseConfig {

    private Users mapUser(ResultSet rs) throws SQLException {
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

        user.setId(rs.getInt("id"));
        return user;
    }
    public boolean updateAvatar(int userId, String avatarUrl) {

        String sql = """
            UPDATE users
            SET avatar_url = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, avatarUrl);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public Users getByID(int id) {
        String sql = """
            SELECT u.*, r.id AS role_id, r.role_name
            FROM users u
            JOIN roles r ON u.role_id = r.id
            WHERE u.id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Users findByEmail(String email) {
        String sql = """
            SELECT u.*, r.id AS role_id, r.role_name
            FROM users u
            JOIN roles r ON u.role_id = r.id
            WHERE u.email = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Users getByUsername(String username) {
        String sql = """
            SELECT u.*, r.id AS role_id, r.role_name
            FROM users u
            JOIN roles r ON u.role_id = r.id
            WHERE u.username = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Users> getAll() {
        List<Users> list = new ArrayList<>();

        String sql = """
            SELECT u.*, r.id AS role_id, r.role_name
            FROM users u
            JOIN roles r ON u.role_id = r.id
            ORDER BY u.id
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void add(Users user) {
        String sql = """
            INSERT INTO users(
                username, password, full_name, avatar_url,
                email, date_of_birth, phone_number, address, role_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getAvatarUrl());
            ps.setString(5, user.getEmail());

            if (user.getDateOfBirth() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(user.getDateOfBirth()));
            }

            ps.setString(7, user.getPhoneNumber());
            ps.setString(8, user.getAddress());
            ps.setInt(9, user.getRole().getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addAndReturnId(Users user) {
        String sql = """
            INSERT INTO users(
                username, password, full_name, avatar_url,
                email, date_of_birth, phone_number, address, role_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getAvatarUrl());
            ps.setString(5, user.getEmail());

            if (user.getDateOfBirth() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(user.getDateOfBirth()));
            }

            ps.setString(7, user.getPhoneNumber());
            ps.setString(8, user.getAddress());
            ps.setInt(9, user.getRole().getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean update(Connection conn ,  int id, Users user) {
        String sql = """
        UPDATE users
        SET
            full_name = ?,
            avatar_url = ?,
            email = ?,
            date_of_birth = ?,
            phone_number = ?,
            address = ?,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = ?
    """;

        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, user.getFullName());
            ps.setString(2, user.getAvatarUrl());
            ps.setString(3, user.getEmail());

            if (user.getDateOfBirth() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, Date.valueOf(user.getDateOfBirth()));
            }

            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getAddress());
            ps.setInt(7, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int userId, Users user) {
        try (Connection conn = getConnection()) {
            return update(conn, userId, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Users getUserByIDForUpdate(Connection conn, int userId) throws SQLException{
        String sql = "SELECT u.*, r.id AS role_id, r.role_name " +
                            "FROM users u " +
                            "JOIN roles r ON u.role_id = r.id " +
                            "WHERE u.id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return  mapUser(rs);
                }
            }
        }
        return null;
    }

    public Users findByEmailExceptId(String email, int userId) {

        String sql = """
        SELECT *
        FROM users
        WHERE email = ?
        AND id <> ?
    """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}