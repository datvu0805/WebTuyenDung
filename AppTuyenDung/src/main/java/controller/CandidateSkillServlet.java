package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import dto.CandidateSkillBatchDTO;
import dto.CandidateSkillDTO;
import mapper.CandidateSkillBatchRequestMapper;
import mapper.CandidateSkillRequestMapper;
import model.Candidates;
import model.Skill;
import service.CandidateSkillService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/candidate-skills/*"})
public class CandidateSkillServlet extends BaseServlet {

    private final CandidateSkillService candidateSkillService = new CandidateSkillService();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /candidate-skills/candidate?id=1
            if ("/candidate".equals(pathInfo)) {

                int candidateId = Integer.parseInt(req.getParameter("id"));

                List<Skill> skills = candidateSkillService.getSkillsByCandidate(candidateId);

                ApiResponse<List<Skill>> response =
                        new ApiResponse<>(true, "Lấy danh sách kỹ năng thành công!", skills);

                resp.setStatus(HttpServletResponse.SC_OK);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }

            // GET /candidate-skills/skill?id=1
            if ("/skill".equals(pathInfo)) {

                int skillId = Integer.parseInt(req.getParameter("id"));

                List<Candidates> candidates =
                        candidateSkillService.getCandidatesBySkill(skillId);

                ApiResponse<List<Candidates>> response =
                        new ApiResponse<>(true, "Lấy danh sách ứng viên thành công!", candidates);

                resp.setStatus(HttpServletResponse.SC_OK);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }

            ApiResponse<Object> response =
                    new ApiResponse<>(false, "Đường dẫn không hợp lệ", null);

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, "ID không hợp lệ", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            CandidateSkillDTO dto = CandidateSkillRequestMapper.toDTO(req);

            candidateSkillService.add(dto);

            resp.setStatus(HttpServletResponse.SC_OK);

            ApiResponse<CandidateSkillDTO> response =
                    new ApiResponse<>(true, "Thêm kỹ năng thành công!", dto);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, "Dữ liệu số không hợp lệ!", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, "Có lỗi hệ thống xảy ra: " + e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } finally {

            resp.getWriter().flush();
        }
    }

//    @Override
//    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        req.setCharacterEncoding("UTF-8");
//        resp.setContentType("application/json");
//        resp.setCharacterEncoding("UTF-8");
//
//        try {
//
//            CandidateSkillBatchDTO dto = CandidateSkillBatchRequestMapper.toDTO(req);
//
//            candidateSkillService.update(dto);
//
//            ApiResponse<CandidateSkillBatchDTO> response =
//                    new ApiResponse<>(true, "Cập nhật kỹ năng thành công!", dto);
//
//            resp.setStatus(HttpServletResponse.SC_OK);
//
//            resp.getWriter().print(objectMapper.writeValueAsString(response));
//
//        } catch (NumberFormatException e) {
//
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//
//            ApiResponse<Object> response =
//                    new ApiResponse<>(false, "Dữ liệu số không hợp lệ!", null);
//
//            resp.getWriter().print(objectMapper.writeValueAsString(response));
//
//        } catch (IllegalArgumentException e) {
//
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//
//            ApiResponse<Object> response =
//                    new ApiResponse<>(false, e.getMessage(), null);
//
//            resp.getWriter().print(objectMapper.writeValueAsString(response));
//
//        } catch (Exception e) {
//
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//
//            ApiResponse<Object> response =
//                    new ApiResponse<>(false, e.getMessage(), null);
//
//            resp.getWriter().print(objectMapper.writeValueAsString(response));
//        }
//    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            CandidateSkillDTO dto = CandidateSkillRequestMapper.toDTO(req);

            candidateSkillService.delete(dto);

            ApiResponse<CandidateSkillDTO> response =
                    new ApiResponse<>(true, "Xóa kỹ năng thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, "Dữ liệu số không hợp lệ!", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response =
                    new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }
}