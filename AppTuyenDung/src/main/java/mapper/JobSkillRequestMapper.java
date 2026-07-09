package mapper;

import dto.JobSkillDTO;

import javax.servlet.http.HttpServletRequest;

public class JobSkillRequestMapper {

    public static JobSkillDTO toDTO(HttpServletRequest req) {

        JobSkillDTO dto = new JobSkillDTO();

        dto.setJobId(Integer.parseInt(req.getParameter("jobId")));
        dto.setSkillId(Integer.parseInt(req.getParameter("skillId")));

        return dto;
    }

}