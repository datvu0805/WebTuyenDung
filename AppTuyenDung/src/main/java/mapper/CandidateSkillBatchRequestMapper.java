package mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CandidateSkillBatchDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CandidateSkillBatchRequestMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static CandidateSkillBatchDTO toDTO(HttpServletRequest req) throws IOException {
        return mapper.readValue(req.getInputStream(), CandidateSkillBatchDTO.class);
    }
}