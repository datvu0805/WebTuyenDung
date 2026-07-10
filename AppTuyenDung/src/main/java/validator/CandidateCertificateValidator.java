package validator;

import constant.ScoreType;
import dao.CertificateDAO;
import dto.CandidateCertificateDTO;
import model.Certificate;

import java.time.LocalDate;

public class CandidateCertificateValidator {

    private static final CertificateDAO certificateDAO = new CertificateDAO();

    public static void validate(CandidateCertificateDTO dto) {

        if (dto.getCandidateId() <= 0) {
            throw new IllegalArgumentException("Candidate không hợp lệ.");
        }

        if (dto.getCertificateId() <= 0) {
            throw new IllegalArgumentException("Certificate không hợp lệ.");
        }

        if (dto.getScore() == null || dto.getScore().isBlank()) {
            throw new IllegalArgumentException("Điểm không được để trống.");
        }

        Certificate certificate = certificateDAO.getById(dto.getCertificateId());

        if (certificate == null) {
            throw new IllegalArgumentException("Chứng chỉ không tồn tại.");
        }

        if (certificate.getScoreType() == ScoreType.NUMERIC) {

            try {
                Double.parseDouble(dto.getScore());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Điểm phải là số.");
            }

        } else {

            String score = dto.getScore().toUpperCase();

            if (!score.matches("A\\+?|B\\+?|C\\+?|D\\+?|F")) {
                throw new IllegalArgumentException("Điểm chữ không hợp lệ.");
            }
        }

        LocalDate issueDate = dto.getIssueDate();
        LocalDate expiryDate = dto.getExpiryDate();

        if (issueDate != null && expiryDate != null) {

            if (expiryDate.isBefore(issueDate)) {
                throw new IllegalArgumentException("Ngày hết hạn phải sau ngày cấp.");
            }

        }

        if (dto.getDescription() != null && dto.getDescription().length() > 255) {
            throw new IllegalArgumentException("Mô tả không được vượt quá 255 ký tự.");
        }

    }

}