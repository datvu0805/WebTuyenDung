package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import dto.CandidateEducationDTO;
import service.CandidateEducationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/candidate-education/*"})
public class CandidateEducationServlet extends BaseServlet {

    private final CandidateEducationService candidateEducationService = new CandidateEducationService();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Lấy candidateId của user đang đăng nhập từ session — chống IDOR (không tin candidateId client gửi lên)
    private Integer getSessionCandidateId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (Integer) session.getAttribute("candidateId");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /candidate-education/candidate?id=1
            if ("/candidate".equals(pathInfo)) {

                int candidateId = Integer.parseInt(req.getParameter("id"));

                List<CandidateEducationDTO> list = candidateEducationService.getByCandidateId(candidateId);

                ApiResponse<List<CandidateEducationDTO>> response =
                        new ApiResponse<>(true, "Lấy danh sách học vấn thành công!", list);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }

            ApiResponse<Object> response = new ApiResponse<>(false, "Đường dẫn không hợp lệ", null);

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "ID không hợp lệ", null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            Integer sessionCandidateId = getSessionCandidateId(req);

            if (sessionCandidateId == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Chỉ ứng viên mới có thể thực hiện hành động này.", null)));
                return;
            }

            CandidateEducationDTO dto = objectMapper.readValue(req.getReader(), CandidateEducationDTO.class);
            dto.setCandidateId(sessionCandidateId);

            candidateEducationService.add(dto);

            ApiResponse<CandidateEducationDTO> response =
                    new ApiResponse<>(true, "Thêm học vấn thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "Có lỗi hệ thống: " + e.getMessage(), null)));

        } finally {

            resp.getWriter().flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            Integer sessionCandidateId = getSessionCandidateId(req);

            if (sessionCandidateId == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Chỉ ứng viên mới có thể thực hiện hành động này.", null)));
                return;
            }

            CandidateEducationDTO dto = objectMapper.readValue(req.getReader(), CandidateEducationDTO.class);
            dto.setCandidateId(sessionCandidateId);

            candidateEducationService.update(dto);

            ApiResponse<CandidateEducationDTO> response =
                    new ApiResponse<>(true, "Cập nhật học vấn thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            Integer sessionCandidateId = getSessionCandidateId(req);

            if (sessionCandidateId == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Chỉ ứng viên mới có thể thực hiện hành động này.", null)));
                return;
            }

            int id = Integer.parseInt(req.getParameter("id"));

            candidateEducationService.delete(id);

            ApiResponse<Integer> response = new ApiResponse<>(true, "Xóa học vấn thành công!", id);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "ID không hợp lệ", null)));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }
}
