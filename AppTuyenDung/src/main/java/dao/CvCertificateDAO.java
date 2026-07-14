package dao;

import config.DatabaseConfig;
import model.CV;
import model.CandidateCertificate;
import model.Candidates;
import model.Certificate;
import model.CvCertificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CvCertificateDAO extends DatabaseConfig {

    // Lấy candidate_id của CV — dùng để kiểm tra ownership trước khi cho sửa
    public Integer getCandidateIdOfCv(int cvId) {

        String sql = "SELECT candidate_id FROM cvs WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cvId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("candidate_id");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void deleteByCvId(int cvId) {

        String sql = "DELETE FROM cv_certificates WHERE cv_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cvId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addBatch(int cvId, List<Integer> candidateCertificateIds) {

        if (candidateCertificateIds == null || candidateCertificateIds.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO cv_certificates(cv_id, candidate_certificate_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Integer candidateCertificateId : candidateCertificateIds) {

                ps.setInt(1, cvId);
                ps.setInt(2, candidateCertificateId);

                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Xóa hết rồi thêm lại — dùng khi candidate cập nhật danh sách chứng chỉ gắn vào 1 CV
    public void replaceForCv(int cvId, List<Integer> candidateCertificateIds) {
        deleteByCvId(cvId);
        addBatch(cvId, candidateCertificateIds);
    }

    // Lấy danh sách chứng chỉ (kèm tên) đã gắn vào 1 CV
    public List<CvCertificate> getByCvId(int cvId) {

        String sql = """
                SELECT cvc.id AS cvc_id, cvc.cv_id, cvc.candidate_certificate_id,
                       cc.candidate_id, cc.certificate_id, cc.score, cc.issue_date, cc.expiry_date,
                       c.certificate_name
                FROM cv_certificates cvc
                JOIN candidate_certificates cc ON cvc.candidate_certificate_id = cc.id
                JOIN certificates c ON cc.certificate_id = c.id
                WHERE cvc.cv_id = ?
                """;

        List<CvCertificate> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cvId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    CandidateCertificate candidateCertificate = new CandidateCertificate(
                            rs.getInt("candidate_certificate_id"),
                            new Candidates(rs.getInt("candidate_id")),
                            new Certificate(rs.getInt("certificate_id"), rs.getString("certificate_name"), null),
                            rs.getString("score"),
                            rs.getDate("issue_date") != null ? rs.getDate("issue_date").toLocalDate() : null,
                            rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null,
                            null
                    );

                    CV cv = new CV();
                    cv.setId(rs.getInt("cv_id"));

                    CvCertificate cvCertificate = new CvCertificate(rs.getInt("cvc_id"), cv, candidateCertificate);

                    list.add(cvCertificate);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
