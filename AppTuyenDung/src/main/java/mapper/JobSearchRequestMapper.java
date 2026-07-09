package mapper;

import dto.JobSearchDTO;

import javax.servlet.http.HttpServletRequest;

public class JobSearchRequestMapper {

    public static JobSearchDTO toDTO(HttpServletRequest req){

        JobSearchDTO dto = new JobSearchDTO();

        dto.setTitle(req.getParameter("title"));

        dto.setLocation(req.getParameter("location"));

        dto.setExperience(req.getParameter("experience"));

        String minSalary=req.getParameter("minSalary");

        if(minSalary!=null&&!minSalary.isBlank()){

            dto.setMinSalary(Double.parseDouble(minSalary));

        }

        String maxSalary=req.getParameter("maxSalary");

        if(maxSalary!=null&&!maxSalary.isBlank()){

            dto.setMaxSalary(Double.parseDouble(maxSalary));

        }

        String status=req.getParameter("status");

        if(status!=null&&!status.isBlank()){

            dto.setStatus(Short.parseShort(status));

        }

        return dto;

    }

}