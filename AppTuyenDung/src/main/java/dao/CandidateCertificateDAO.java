package dao;


import config.DatabaseConfig;
import mapper.CandidateCertificateMapper;
import model.CandidateCertificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CandidateCertificateDAO extends DatabaseConfig implements TDAO<CandidateCertificate> {


    @Override
    public CandidateCertificate getById(int id) {

        String sql = "SELECT * FROM candidate_certificates WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return CandidateCertificateMapper.map(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void add(CandidateCertificate entity) {

        String sql = """
            INSERT INTO candidate_certificates
            (candidate_id, certificate_id, score, issue_date, expiry_date, description)
            VALUES (?, ?, ?, ?, ?, ?) 
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getCandidateID().getId());
            ps.setInt(2, entity.getCertificatesID().getId());
            ps.setString(3, entity.getScore());
            ps.setObject(4, entity.getIssueDate());
            ps.setObject(5, entity.getExpiryDate());
            ps.setString(6, entity.getDescription());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(CandidateCertificate entity) {

        String sql = """
            UPDATE candidate_certificates
            SET candidate_id = ?,
                certificate_id = ?,
                score = ?,
                issue_date = ?,
                expiry_date = ?,
                description = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getCandidateID().getId());
            ps.setInt(2, entity.getCertificatesID().getId());
            ps.setString(3, entity.getScore());
            ps.setObject(4, entity.getIssueDate());
            ps.setObject(5, entity.getExpiryDate());
            ps.setString(6, entity.getDescription());
            ps.setInt(7, entity.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM candidate_certificates WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CandidateCertificate> getAll() {

        String sql = "SELECT * FROM candidate_certificates";

        List<CandidateCertificate> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(CandidateCertificateMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
