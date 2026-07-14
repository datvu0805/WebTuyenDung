package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import model.CvEducation;
import service.CvEducationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/cv-educations/*"})
public class CvEducationServlet extends BaseServlet {

    private final CvEducationService cvEducationService = new CvEducationService();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private Integer getSessionCandidateId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (Integer) session.getAttribute("candidateId");
    }

    // Chỉ candidate sở hữu CV mới được sửa danh sách học vấn gắn vào CV đó
    private boolean isOwnerOfCv(HttpServletRequest req, int cvId) {

        Integer sessionCandidateId = getSessionCandidateId(req);

        if (sessionCandidateId == null) return false;

        Integer cvCandidateId = cvEducationService.getCandidateIdOfCv(cvId);

        return cvCandidateId != null && cvCandidateId.equals(sessionCandidateId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String cvIdParam = req.getParameter("cvId");

            if (cvIdParam == null || cvIdParam.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Thiếu cvId", null)));
                return;
            }

            int cvId = Integer.parseInt(cvIdParam);

            List<CvEducation> list = cvEducationService.getByCvId(cvId);

            ApiResponse<List<CvEducation>> response =
                    new ApiResponse<>(true, "Lấy danh sách học vấn gắn CV thành công!", list);

            resp.setStatus(HttpServletResponse.SC_OK);
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

    // PUT /cv-educations — body: { "cvId": 1, "candidateEducationIds": [1,2] }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            Map<String, Object> body = objectMapper.readValue(req.getReader(), Map.class);

            int cvId = ((Number) body.get("cvId")).intValue();

            if (!isOwnerOfCv(req, cvId)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Bạn không có quyền chỉnh sửa CV này.", null)));
                return;
            }

            @SuppressWarnings("unchecked")
            List<Integer> candidateEducationIds = ((List<Number>) body.getOrDefault("candidateEducationIds", List.of()))
                    .stream().map(Number::intValue).collect(java.util.stream.Collectors.toList());

            cvEducationService.replaceForCv(cvId, candidateEducationIds);

            ApiResponse<Object> response = new ApiResponse<>(true, "Cập nhật học vấn cho CV thành công!", null);

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
}
