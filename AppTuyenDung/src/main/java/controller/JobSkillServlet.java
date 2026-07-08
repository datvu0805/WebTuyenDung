package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.JobSkillDTO;
import mapper.JobSkillRequestMapper;
import service.JobSkillService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/job-skills/*"})
public class JobSkillServlet extends BaseServlet {

    private final JobSkillService jobSkillService = new JobSkillService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            JobSkillDTO dto = JobSkillRequestMapper.toDTO(req);

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