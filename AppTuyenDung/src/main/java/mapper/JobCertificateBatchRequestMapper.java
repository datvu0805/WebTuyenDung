package mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.JobCertificateBatchDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JobCertificateBatchRequestMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JobCertificateBatchDTO toDTO(HttpServletRequest req) throws IOException {
        return mapper.readValue(req.getInputStream(), JobCertificateBatchDTO.class);
    }
}