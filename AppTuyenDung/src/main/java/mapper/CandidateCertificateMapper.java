package mapper;

import dto.CandidateCertificateDTO;
import model.CandidateCertificate;
import model.Candidates;
import model.Certificate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CandidateCertificateMapper {

    public static CandidateCertificate map(ResultSet rs) throws SQLException {

        return new CandidateCertificate(
                rs.getInt("id"),
                new Candidates(rs.getInt("candidate_id")),
                new Certificate(rs.getInt("certificate_id")),
                rs.getString("score"),
                rs.getDate("issue_date").toLocalDate(),
                rs.getDate("expiry_date").toLocalDate(),
                rs.getString("description")
        );
    }
    public static CandidateCertificateDTO toDTO(CandidateCertificate entity) {

        if (entity == null) {
            return null;
        }

        return new CandidateCertificateDTO(
                entity.getId(),
                entity.getCandidateID().getId(),
                entity.getCertificatesID().getId(),
                entity.getScore(),
                entity.getIssueDate(),
                entity.getExpiryDate(),
                entity.getDescription()
        );
    }
    public static CandidateCertificate toEntity(CandidateCertificateDTO dto) {

        if (dto == null) {
            return null;
        }

        return new CandidateCertificate(
                dto.getId(),
                new Candidates(dto.getCandidateId()),
                new Certificate(dto.getCertificateId()),
                dto.getScore(),
                dto.getIssueDate(),
                dto.getExpiryDate(),
                dto.getDescription()
        );
    }
}