package dao;

import config.DatabaseConfig;
import model.Company;
import model.Employers;
import model.Role;
import model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployerDAO extends DatabaseConfig {

    public int add(int userId, int companyId) {
        String sql = """
            INSERT INTO employers(user_id, company_id)
            VALUES (?, ?)
            RETURNING id
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            ps.setInt(2, companyId);

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

    private Employers mapEmployer(ResultSet rs) throws SQLException {
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

        Company company = new Company(
                rs.getString("company_name"),
                rs.getString("description")
        );
        company.setId(rs.getInt("company_id"));

        Employers employer = new Employers(
                rs.getInt("employer_id"),
                user,
                company,
                role
        );

        return employer;
    }

    private String baseSelectSql() {
        return """
            SELECT
                e.id AS employer_id,
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
                r.role_name,
                c.id AS company_id,
                c.company_name,
                c.description
            FROM employers e
            JOIN users u ON e.user_id = u.id
            JOIN roles r ON u.role_id = r.id
            JOIN companies c ON e.company_id = c.id
        """;
    }

    public void update(Employers employer) {
        String sql = """
            UPDATE employers
            SET user_id = ?,
                company_id = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, employer.getUser().getId());
            ps.setInt(2, employer.getCompany().getId());
            ps.setInt(3, employer.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM employers WHERE id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Employers> getAll() {
        List<Employers> employers = new ArrayList<>();

        String sql = baseSelectSql() + " ORDER BY e.id DESC";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                employers.add(mapEmployer(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employers;
    }

    public Employers findByUserId(int userId) {
        String sql = baseSelectSql() + " WHERE u.id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployer(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Employers findById(int id) {
        String sql = baseSelectSql() + " WHERE e.id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployer(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Employers> findByCompanyId(int companyId) {
        List<Employers> employers = new ArrayList<>();

        String sql = baseSelectSql() + """
            WHERE c.id = ?
            ORDER BY e.id DESC
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employers.add(mapEmployer(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employers;
    }
}