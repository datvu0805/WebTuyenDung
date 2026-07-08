package mapper;

import dto.SkillDTO;

import javax.servlet.http.HttpServletRequest;

public class SkillRequestMapper {

    public static SkillDTO toDTO(HttpServletRequest req) {

        SkillDTO dto = new SkillDTO();

        String id = req.getParameter("id");
        if (id != null && !id.isBlank()) {
            dto.setId(Integer.parseInt(id));
        }

        dto.setSkillName(req.getParameter("skillName"));

        return dto;
    }
}