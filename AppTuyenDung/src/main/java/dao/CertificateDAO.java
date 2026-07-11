package dao;

import config.DatabaseConfig;
import mapper.CertificateMapper;
import model.Certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CertificateDAO extends DatabaseConfig implements TDAO<Certificate> {

    @Override
    public void add(Certificate certificate) {

        String sql = """
        INSERT INTO certificates(certificate_name, score_type,created_at,
                    updated_at)
        VALUES (?, ?,?,?)
        RETURNING id
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, certificate.getCertificatesName());
            ps.setString(2, certificate.getScoreType().name());
            ps.setObject(3, certificate.getCreatedAt());
            ps.setObject(4, certificate.getUpdatedAt());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    certificate.setId(rs.getInt("id"));
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Certificate entity) {

        String sql = """
                UPDATE certificates
                SET certificate_name = ?, score_type = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getCertificatesName());
            ps.setString(2, entity.getScoreType().name());
            ps.setInt(3, entity.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM certificates WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Certificate getById(int id) {

        String sql = "SELECT * FROM certificates WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return CertificateMapper.map(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<Certificate> getAll() {

        String sql = "SELECT * FROM certificates";

        List<Certificate> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(CertificateMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}