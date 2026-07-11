package mapper;

import dto.JobDTO;
import model.Employers;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class JobRequestMapper {

    public static JobDTO toDTO(HttpServletRequest req) {

        JobDTO dto = new JobDTO();

        // Employer
        String employerId = req.getParameter("employerId");
        if (employerId != null && !employerId.isBlank()) {
            dto.setEmployerId(Integer.parseInt(employerId));
        }

        // Thông tin cơ bản
        dto.setTitle(req.getParameter("title"));
        dto.setDescription(req.getParameter("description"));
        dto.setLocation(req.getParameter("location"));
        dto.setExperience(req.getParameter("experience"));
        dto.setCurrency(req.getParameter("currency"));

        // Salary
        String minSalary = req.getParameter("minSalary");
        if (minSalary != null && !minSalary.isBlank()) {
            dto.setMinSalary(Double.parseDouble(minSalary));
        }

        String maxSalary = req.getParameter("maxSalary");
        if (maxSalary != null && !maxSalary.isBlank()) {
            dto.setMaxSalary(Double.parseDouble(maxSalary));
        }

        // Quantity
        String quantity = req.getParameter("quantity");
        if (quantity != null && !quantity.isBlank()) {
            dto.setQuantity(Integer.parseInt(quantity));
        }

        // DateTime
        String postedAt = req.getParameter("postedAt");
        if (postedAt != null && !postedAt.isBlank()) {
            dto.setPostedAt(LocalDateTime.parse(postedAt));
        }

        String expiredAt = req.getParameter("expiredAt");
        if (expiredAt != null && !expiredAt.isBlank()) {
            dto.setExpiredAt(LocalDateTime.parse(expiredAt));
        }

        String applicationDeadline = req.getParameter("applicationDeadline");
        if (applicationDeadline != null && !applicationDeadline.isBlank()) {
            dto.setApplicationDeadline(LocalDateTime.parse(applicationDeadline));
        }

        // Status
        String status = req.getParameter("status");
        if (status != null && !status.isBlank()) {
            dto.setStatus(Short.parseShort(status));
        }

        // Hidden
        String hidden = req.getParameter("hiddenOnExpiry");
        if (hidden != null && !hidden.isBlank()) {
            dto.setHiddenOnExpiry(Boolean.parseBoolean(hidden));
        }else {
            dto.setHiddenOnExpiry(false);
        }

        // companyId
        String companyId = req.getParameter("companyId");
        if (companyId != null && !companyId.isBlank()) {
            try { dto.setCompanyId(Integer.parseInt(companyId)); } catch (NumberFormatException ignored) {}
        }

        // jobPositionId
        String jobPositionId = req.getParameter("jobPositionId");
        if (jobPositionId != null && !jobPositionId.isBlank()) {
            try { dto.setJobPositionId(Integer.parseInt(jobPositionId)); } catch (NumberFormatException ignored) {}
        }

        return dto;
    }

}