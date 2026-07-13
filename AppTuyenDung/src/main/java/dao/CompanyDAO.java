package dao;

import config.DatabaseConfig;
import model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO extends DatabaseConfig {

    private Company mapCompany(ResultSet rs) throws SQLException {
        Company company = new Company();

        company.setId(rs.getInt("id"));
        company.setCompanyName(rs.getString("company_name"));
        company.setDescription(rs.getString("description"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        if (createdAt != null) {
            company.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            company.setUpdatedAt(updatedAt.toLocalDateTime());
        }

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
            throw new RuntimeException("Không thể tạo công ty", e);
        }

        return -1;
    }

    public boolean update(Company company) {
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

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Không thể cập nhật công ty", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM companies WHERE id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Không thể xóa công ty", e);
        }
    }

    public Company findById(int id) {
        String sql = """
            SELECT id, company_name, description, created_at, updated_at
            FROM companies
            WHERE id = ?
        """;

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
            throw new RuntimeException("Không thể tìm công ty", e);
        }

        return null;
    }

    public List<Company> getAll() {
        List<Company> companies = new ArrayList<>();

        String sql = """
            SELECT id, company_name, description, created_at, updated_at
            FROM companies
            ORDER BY id DESC
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                companies.add(mapCompany(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể lấy danh sách công ty",
                    e
            );
        }

        return companies;
    }

    public int countCompanies() {
        String sql = "SELECT count(*) FROM companies";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("count(*)");
            }

        } catch (SQLException e) {
        }
        return 0;
    }
}