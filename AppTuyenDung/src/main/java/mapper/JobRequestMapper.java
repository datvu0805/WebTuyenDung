package mapper;

import dto.JobDTO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class JobRequestMapper {

    public static JobDTO toDTO(HttpServletRequest req){

        JobDTO dto = new JobDTO();

        dto.setEmployerId(Integer.parseInt(req.getParameter("employerId")));
        dto.setTitle(req.getParameter("title"));
        dto.setDescription(req.getParameter("description"));
        dto.setSalary(Double.parseDouble(req.getParameter("salary")));
        dto.setLocation(req.getParameter("location"));
        dto.setExperience(req.getParameter("experience"));
        dto.setQuantity(Integer.parseInt(req.getParameter("quantity")));

        dto.setPostedAt(LocalDateTime.parse(req.getParameter("postedAt")));
        dto.setExpiredAt(LocalDateTime.parse(req.getParameter("expiredAt")));
        dto.setApplicationDeadline(LocalDateTime.parse(req.getParameter("applicationDeadline")));

        dto.setStatus(Short.parseShort(req.getParameter("status")));
        dto.setHiddenOnExpiry(Boolean.parseBoolean(req.getParameter("hiddenOnExpiry")));

        return dto;
    }
}