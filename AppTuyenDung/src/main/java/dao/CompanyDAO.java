package dao;

import config.DatabaseConfig;
import model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO extends DatabaseConfig {

    private Company mapCompany(ResultSet rs) throws SQLException {
        Company company = new Company(
                rs.getString("company_name"),
                rs.getString("description")
        );
        company.setId(rs.getInt("id"));
        return company;
    }

    public int add(Company company) {
        String sql = """
            INSERT INTO companies(company_name, description)
            VALUES (?, ?)
            RETURNING id
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, company.getCompanyName());
            ps.setString(2, company.getDescription());

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

    public void update(Company company) {
        String sql = """
            UPDATE companies
            SET company_name = ?,
                description = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, company.getCompanyName());
            ps.setString(2, company.getDescription());
            ps.setInt(3, company.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM companies WHERE id = ?";

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

    public Company findByName(String name) {
        String sql = "SELECT * FROM companies WHERE company_name = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCompany(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public Company findById(int id) {
        String sql = "SELECT * FROM companies WHERE id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCompany(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Company> getAll() {
        List<Company> companies = new ArrayList<>();

        String sql = "SELECT * FROM companies ORDER BY id DESC";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                companies.add(mapCompany(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }

    public List<Company> findAll() {
        return getAll();
    }
}