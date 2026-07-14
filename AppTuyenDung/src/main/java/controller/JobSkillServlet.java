package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.JobDAO;
import dto.ApiResponse;
import dto.JobSkillDTO;
import mapper.JobSkillRequestMapper;
import model.Job;
import service.JobSkillService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/job-skills/*"})
public class JobSkillServlet extends BaseServlet {

    private final JobSkillService jobSkillService = new JobSkillService();
    private final JobDAO jobDAO = new JobDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Kiểm tra employer đang đăng nhập có sở hữu job này không — chống việc employer A sửa job của employer B
    private boolean isOwnerOfJob(HttpServletRequest req, Integer jobId) {

        if (jobId == null) return false;

        HttpSession session = req.getSession(false);

        if (session == null) return false;

        // ADMIN được thao tác trên mọi job
        if ("ADMIN".equals(session.getAttribute("role"))) return true;

        Object employerId = session.getAttribute("employerId");

        if (employerId == null) return false;

        Job job = jobDAO.getById(jobId);

        return job != null && job.getEmployerID() != null
                && job.getEmployerID().getId() == (int) employerId;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            JobSkillDTO dto = JobSkillRequestMapper.toDTO(req);

            if (!isOwnerOfJob(req, dto.getJobId())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Bạn không có quyền chỉnh sửa kỹ năng của công việc này.", null)));
                return;
            }

            jobSkillService.add(dto);

            ApiResponse<JobSkillDTO> response = new ApiResponse<>(true, "Thêm kỹ năng cho công việc thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, "Có lỗi hệ thống: " + e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            JobSkillDTO dto = JobSkillRequestMapper.toDTO(req);

            if (!isOwnerOfJob(req, dto.getJobId())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Bạn không có quyền chỉnh sửa kỹ năng của công việc này.", null)));
                return;
            }

            jobSkillService.delete(dto);

            ApiResponse<JobSkillDTO> response = new ApiResponse<>(true, "Xóa kỹ năng khỏi công việc thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, "Có lỗi hệ thống: " + e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String jobId = req.getParameter("jobId");
            String skillId = req.getParameter("skillId");

            if (jobId != null && !jobId.isBlank()) {

                List<JobSkillDTO> list = jobSkillService.getByJobId(Integer.parseInt(jobId));

                ApiResponse<List<JobSkillDTO>> response = new ApiResponse<>(true, "Lấy danh sách kỹ năng của công việc thành công!", list);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));

            } else if (skillId != null && !skillId.isBlank()) {

                List<JobSkillDTO> list = jobSkillService.getBySkillId(Integer.parseInt(skillId));

                ApiResponse<List<JobSkillDTO>> response = new ApiResponse<>(true, "Lấy danh sách công việc theo kỹ năng thành công!", list);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));

            } else {

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                ApiResponse<Object> response = new ApiResponse<>(false, "Vui lòng truyền jobId hoặc skillId", null);

                resp.getWriter().print(objectMapper.writeValueAsString(response));
            }

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, "ID không hợp lệ", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }
}