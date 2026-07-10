package mapper;

import dto.CandidateCertificateDTO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

public class CandidateCertificateRequestMapper {

    public static CandidateCertificateDTO toDTO(HttpServletRequest req) {

        CandidateCertificateDTO dto = new CandidateCertificateDTO();

        String id = req.getParameter("id");
        if (id != null && !id.isBlank()) {
            dto.setId(Integer.parseInt(id));
        }

        dto.setCandidateId(Integer.parseInt(req.getParameter("candidateId")));

        dto.setCertificateId(Integer.parseInt(req.getParameter("certificateId")));

        dto.setScore(req.getParameter("score"));

        String issueDate = req.getParameter("issueDate");
        if (issueDate != null && !issueDate.isBlank()) {
            dto.setIssueDate(LocalDate.parse(issueDate));
        }

        String expiryDate = req.getParameter("expiryDate");
        if (expiryDate != null && !expiryDate.isBlank()) {
            dto.setExpiryDate(LocalDate.parse(expiryDate));
        }

        dto.setDescription(req.getParameter("description"));

        return dto;
    }

}