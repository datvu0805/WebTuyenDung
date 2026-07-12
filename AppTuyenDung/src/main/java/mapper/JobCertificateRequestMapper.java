package mapper;

import dto.JobCertificateDTO;

import javax.servlet.http.HttpServletRequest;

public class JobCertificateRequestMapper {

    public static JobCertificateDTO toDTO(HttpServletRequest req) {

        JobCertificateDTO dto = new JobCertificateDTO();

        String jobId = req.getParameter("jobId");
        String certificateId = req.getParameter("certificateId");

        if (jobId != null && !jobId.trim().isEmpty()) {
            dto.setJobId(Integer.parseInt(jobId));
        }

        if (certificateId != null && !certificateId.trim().isEmpty()) {
            dto.setCertificateId(Integer.parseInt(certificateId));
        }

        return dto;
    }
}