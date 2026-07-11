package mapper;

import dto.CandidateSkillDTO;

import javax.servlet.http.HttpServletRequest;

public class CandidateSkillRequestMapper {

    public static CandidateSkillDTO toDTO(HttpServletRequest req) {

        CandidateSkillDTO dto = new CandidateSkillDTO();

        String candidateId = req.getParameter("candidateId");
        String skillId = req.getParameter("skillId");

        if (candidateId != null && !candidateId.trim().isEmpty()) {
            dto.setCandidateId(Integer.parseInt(candidateId));
        }

        if (skillId != null && !skillId.trim().isEmpty()) {
            dto.setSkillId(Integer.parseInt(skillId));
        }

        return dto;
    }
}