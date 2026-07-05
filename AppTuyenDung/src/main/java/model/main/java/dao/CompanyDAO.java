package model.main.java.dao;

import model.main.java.config.DatabaseConfig;
import model.main.java.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO extends DatabaseConfig implements IDAO<Company> {

    @Override
    public void add(Company entity) {
        String sql = "  INSERT INTO companies(company_name, description) VALUES (?, ?)";

        try  (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
                ){
            ps.setString(1, entity.getCompanyName());
            ps.setString(2, entity.getDescription());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Company company) {
        String sql = """
                UPDATE companies
                SET company_name = ?,
                    description = ?,
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

    @Override
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

    @Override
    public List<Company> getAll() {
        return List.of();
    }


    public Company findById(int id) {
        String sql = "SELECT * FROM companies WHERE id = ?";

        try (
                Connection conn =getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Company(
                            rs.getString("company_name"),
                            rs.getString("description")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();

        String sql = "SELECT * FROM companies ORDER BY id DESC";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Company company = new Company(
                                rs.getString("company_name"),
                                rs.getString("description")
                        );
                companies.add(company);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }
}
