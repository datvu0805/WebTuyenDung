package mapper;

import dto.CertificateDTO;

import javax.servlet.http.HttpServletRequest;

public class CertificateRequestMapper {

    public static CertificateDTO toDTO(HttpServletRequest req){

        CertificateDTO dto = new CertificateDTO();

        String id = req.getParameter("id");

        if(id != null && !id.isBlank()){
            dto.setId(Integer.parseInt(id));
        }

        dto.setCertificateName(req.getParameter("certificateName"));
        dto.setScoreType(req.getParameter("scoreType"));

        return dto;
    }

}