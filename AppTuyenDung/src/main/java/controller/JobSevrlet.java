package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import dto.JobDTO;
import mapper.JobRequestMapper;
import service.JobService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/jobs/*"})
public class JobSevrlet extends BaseServlet {
    private final JobService jobService = new JobService();
    // chuyển đổi để json đọc được data từ kiểu localdatetime
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /jobs
            if (pathInfo == null || pathInfo.equals("/")) {

                List<JobDTO> jobs = jobService.getAllJobs();

                ApiResponse<List<JobDTO>> response = new ApiResponse<>(true, "Lấy danh sách công việc thành công!", jobs);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));

            } else {

                // GET /jobs/5
                int id = Integer.parseInt(pathInfo.substring(1));

                JobDTO jobDTO = jobService.getJobById(id);

                if (jobDTO == null) {

                    ApiResponse<JobDTO> response = new ApiResponse<>(false, "Không tìm thấy công việc", null);

                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print(objectMapper.writeValueAsString(response));
                    return;
                }

                ApiResponse<JobDTO> response = new ApiResponse<>(true, "Lấy công việc thành công!", jobDTO);

                resp.setStatus(HttpServletResponse.SC_OK);
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


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Cấu hình request và response
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Chuyển request -> DTO
            JobDTO dto = JobRequestMapper.toDTO(req);

            // Gọi service thêm dữ liệu
            jobService.addJob(dto);

            // HTTP 200 OK
            resp.setStatus(HttpServletResponse.SC_OK);

            // Trả về JSON có data
            ApiResponse<JobDTO> apiResponse = new ApiResponse<>(true, "Thêm công việc thành công!", dto);

            // Chuyển object thành JSON
            String jsonResult = objectMapper.writeValueAsString(apiResponse);

            // Gửi về client
            resp.getWriter().print(jsonResult);

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> apiResponse = new ApiResponse<>(false, "Dữ liệu số không hợp lệ hoặc bị trống!", null);

            String jsonResult = objectMapper.writeValueAsString(apiResponse);
            resp.getWriter().print(jsonResult);

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> apiResponse = new ApiResponse<>(false, e.getMessage(), null);

            String jsonResult = objectMapper.writeValueAsString(apiResponse);
            resp.getWriter().print(jsonResult);

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> apiResponse = new ApiResponse<>(false, "Có lỗi hệ thống xảy ra: " + e.getMessage(), null);

            String jsonResult = objectMapper.writeValueAsString(apiResponse);
            resp.getWriter().print(jsonResult);

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

            int id = Integer.parseInt(req.getParameter("id"));

            JobDTO dto = JobRequestMapper.toDTO(req);

            jobService.updateJob(id, dto);

            ApiResponse<JobDTO> response = new ApiResponse<>(true, "Cập nhật công việc thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, "Dữ liệu số không hợp lệ", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            int id = Integer.parseInt(req.getParameter("id"));

            jobService.deleteJob(id);

            ApiResponse<Integer> response = new ApiResponse<>(true, "Xóa công việc thành công!", id);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, "ID không hợp lệ", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }
}
