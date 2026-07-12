package dao;

import config.DatabaseConfig;
import dto.CandidateProfileCVDTO;
import model.CV;
import model.Candidates;
import model.Role;
import model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO extends DatabaseConfig {

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

    public List<Candidates> findAll() {

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
                    ORDER BY c.id DESC
                """;

        List<Candidates> candidates = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                candidates.add(mapCandidate(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return candidates;
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
                        u.email,
                        u.full_name,
                        u.avatar_url,
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

    public Candidates getByID(int id) {
        String sql = "SELECT c.id as candidate_id, u.full_name, u.email " +
                "FROM candidates c " +
                "JOIN users u ON c.user_id = u.id WHERE c.id = ?";
        try (Connection conn = getConnection()) {
            Candidates candidates = null;
            try (
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                // lấy ra thông tin của ứng viên
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        candidates = new Candidates(rs.getInt("candidate_id"));

                        Users users = new Users();
                        users.setFullName(rs.getString("full_name"));
                        users.setEmail(rs.getString("email"));
                        candidates.setUser(users);
                    }
                }
            }

            // lấy ra danh sách cv
            if(candidates != null){
                List<CV> cvList = new ArrayList<>();
                String sqlCV = "SELECT id, cv_title, file_url, description, version " +
                        "FROM cvs WHERE candidate_id = ? ORDER BY id DESC";

                try (PreparedStatement psCV = conn.prepareStatement(sqlCV)) {
                    psCV.setInt(1, candidates.getId());
                    try (ResultSet rs = psCV.executeQuery()){
                        while (rs.next()){
                            CV cv = new CV();
                            cv.setId(rs.getInt("id"));
                            cv.setCvTitle(rs.getString("cv_title"));
                            cv.setFileUrl(rs.getString("file_url"));
                            cv.setDescription(rs.getString("description"));
                            cv.setVersion(rs.getString("version"));

                            cv.setCandidateId(candidates);
                            cvList.add(cv);
                        }
                    }
                }
                return new CandidateProfileCVDTO(candidates, cvList).getCandidateInfo();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi DB profile: " + e.getMessage());
        }
        return null;
    }
}