package dao;

import config.DatabaseConfig;
import model.Company;
import model.Employers;
import model.Role;
import model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployerDAO extends DatabaseConfig implements IDAO<Employers> {
    @Override
    public void add(Employers employer) {
        String sql = "INSERT INTO employers(user_id,company_id) VALUES (?,?)";
        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
                ){
            ps.setInt(1, employer.getUser().getId());
            ps.setInt(2, employer.getCompany().getId());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Employers employer) {
    String sql = "UPDATE employers SET user_id=?,company_id=? WHERE id=?";

    try( Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, employer.getUser().getId());
        ps.setInt(2, employer.getCompany().getId());
        ps.setInt(3, employer.getId());
        ps.executeUpdate();
    }catch (SQLException e){
        e.printStackTrace();
    }
    }

    @Override
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


    @Override
    public List<Employers> getAll() {
        String sql = "SELECT e.id AS employer_id,\n" +
                "                    e.created_at AS employer_created_at,\n" +
                "                    e.updated_at AS employer_updated_at,\n" +
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
                "                    r.name AS role_name,\n" +
                "\n" +
                "                    c.id AS company_id,\n" +
                "                    c.company_name,\n" +
                "                    c.description,\n" +
                "                    c.created_at AS company_created_at,\n" +
                "                    c.updated_at AS company_updated_at\n" +
                "\n" +
                "                FROM employers e\n" +
                "                JOIN users u ON e.user_id = u.id\n" +
                "                JOIN roles r ON u.role_id = r.id\n" +
                "                JOIN companies c ON e.company_id = c.id\n" +
                "                ORDER BY e.id DESC";
        List<Employers> employers = new ArrayList<>();
        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()) {
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
                Company company = new Company(
                        rs.getInt("id"),
                         rs.getString("company_name")
                );
                Role role = new Role(
                        rs.getString("role_name")
                );
                Employers employer = new Employers(
                        rs.getInt("id"),
                        user,company,role
                );
                employers.add(employer);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return employers;
    }


    public Employers findByUserId(int userId) {
        String sql = """
                SELECT
                    e.id AS employer_id,
                    e.created_at AS employer_created_at,
                    e.updated_at AS employer_updated_at,

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
                    r.name AS role_name,

                    c.id AS company_id,
                    c.company_name,
                    c.description,
                    c.created_at AS company_created_at,
                    c.updated_at AS company_updated_at

                FROM employers e
                JOIN users u ON e.user_id = u.id
                JOIN roles r ON u.role_id = r.id
                JOIN companies c ON e.company_id = c.id
                WHERE u.id = ?
                """;

        try (
                Connection conn =getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                    Company company = new Company(
                            rs.getInt("id"),
                            rs.getString("company_name")
                    );
                    Role role = new Role(
                            rs.getString("role_name")
                    );
                    Employers employer = new Employers(
                            rs.getInt("id"),
                            user,company,role
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Employers findById(int id) {
        String sql = """
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
                    r.name AS role_name,

                    c.id AS company_id,
                    c.company_name,
                    c.description,
                

                FROM employers e
                JOIN users u ON e.user_id = u.id
                JOIN roles r ON u.role_id = r.id
                JOIN companies c ON e.company_id = c.id
                WHERE e.id = ?
                """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                    Company company = new Company(
                            rs.getInt("id"),
                            rs.getString("company_name")
                    );
                    Role role = new Role(
                            rs.getString("role_name")
                    );
                    Employers employer = new Employers(
                            rs.getInt("id"),
                            user,company,role
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Employers> findByCompanyId(int companyId) {
        List<Employers> employers = new ArrayList<>();

        String sql = """
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
                    r.name AS role_name,

                    c.id AS company_id,
                    c.company_name,
                    c.description,
              

                FROM employers e
                JOIN users u ON e.user_id = u.id
                JOIN roles r ON u.role_id = r.id
                JOIN companies c ON e.company_id = c.id
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
                    Company company = new Company(
                            rs.getInt("id"),
                            rs.getString("company_name")
                    );
                    Role role = new Role(
                            rs.getString("role_name")
                    );
                    Employers employer = new Employers(
                            rs.getInt("id"),
                            user,company,role
                    );
                    employers.add(employer);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employers;
    }
}
