package mapper;

import constant.ScoreType;
import dto.CertificateDTO;
import model.Certificate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CertificateMapper {

    public static Certificate map(ResultSet rs) throws SQLException {

        return new Certificate(
                rs.getInt("id"),
                rs.getString("certificate_name"),
                ScoreType.valueOf(rs.getString("score_type"))
        );
    }
    public static CertificateDTO toDTO(Certificate certificate){

        if(certificate == null){
            return null;
        }

        return new CertificateDTO(
                certificate.getId(),
                certificate.getCertificatesName(),
                certificate.getScoreType().name()
        );
    }
    public static Certificate toEntity(CertificateDTO dto){

        if(dto == null){
            return null;
        }

        return new Certificate(
                dto.getId(),
                dto.getCertificateName(),
                ScoreType.valueOf(dto.getScoreType().toUpperCase())
        );
    }
}