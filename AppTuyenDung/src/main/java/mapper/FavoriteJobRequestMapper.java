package mapper;

import dto.FavoriteJobDTO;

import javax.servlet.http.HttpServletRequest;

public class FavoriteJobRequestMapper {

    public static FavoriteJobDTO toDTO(HttpServletRequest req) {

        FavoriteJobDTO dto = new FavoriteJobDTO();

        String candidateId = req.getParameter("candidateId");
        String jobId = req.getParameter("jobId");

        if (candidateId != null && !candidateId.trim().isEmpty()) {
            dto.setCandidateId(Integer.parseInt(candidateId));
        }

        if (jobId != null && !jobId.trim().isEmpty()) {
            dto.setJobId(Integer.parseInt(jobId));
        }

        return dto;
    }
}