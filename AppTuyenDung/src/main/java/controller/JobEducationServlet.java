package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.JobDAO;
import dto.ApiResponse;
import dto.JobEducationDTO;
import model.EducationLevel;
import model.Job;
import service.JobEducationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/job-educations/*"})
public class JobEducationServlet extends BaseServlet {

    private final JobEducationService jobEducationService = new JobEducationService();
    private final JobDAO jobDAO = new JobDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Kiểm tra employer đang đăng nhập có sở hữu job này không — chống việc employer A sửa job của employer B
    private boolean isOwnerOfJob(HttpServletRequest req, Integer jobId) {

        if (jobId == null) return false;

        HttpSession session = req.getSession(false);

        if (session == null) return false;

        if ("ADMIN".equals(session.getAttribute("role"))) return true;

        Object employerId = session.getAttribute("employerId");

        if (employerId == null) return false;

        Job job = jobDAO.getById(jobId);

        return job != null && job.getEmployerID() != null
                && job.getEmployerID().getId() == (int) employerId;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /job-educations/job?id=1
            if ("/job".equals(pathInfo)) {

                int jobId = Integer.parseInt(req.getParameter("id"));

                List<EducationLevel> list = jobEducationService.getEducationLevelsByJob(jobId);

                ApiResponse<List<EducationLevel>> response =
                        new ApiResponse<>(true, "Lấy danh sách trình độ học vấn thành công!", list);

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            JobEducationDTO dto = objectMapper.readValue(req.getReader(), JobEducationDTO.class);

            if (!isOwnerOfJob(req, dto.getJobId())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Bạn không có quyền chỉnh sửa yêu cầu học vấn của công việc này.", null)));
                return;
            }

            jobEducationService.add(dto);

            ApiResponse<Object> response = new ApiResponse<>(true, "Thêm yêu cầu học vấn thành công!", null);

            resp.setStatus(HttpServletResponse.SC_CREATED);
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

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            JobEducationDTO dto = objectMapper.readValue(req.getReader(), JobEducationDTO.class);

            if (!isOwnerOfJob(req, dto.getJobId())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Bạn không có quyền chỉnh sửa yêu cầu học vấn của công việc này.", null)));
                return;
            }

            jobEducationService.delete(dto);

            ApiResponse<Object> response = new ApiResponse<>(true, "Xóa yêu cầu học vấn thành công!", null);

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
