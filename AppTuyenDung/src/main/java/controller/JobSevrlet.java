package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.JobDTO;
import mapper.JobRequestMapper;
import model.Jobs;
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
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/plain;charset=UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /jobs
            if (pathInfo == null || pathInfo.equals("/")) {

                List<Jobs> jobs = jobService.getAllJobs();

                if (jobs.isEmpty()) {
                    resp.getWriter().println("Không có công việc nào.");
                    return;
                }

                for (Jobs job : jobs) {
                    resp.getWriter().println("ID: " + job.getId());
                    resp.getWriter().println("Title: " + job.getTitle());
                    resp.getWriter().println("Salary: " + job.getSalary());
                    resp.getWriter().println("----------------------");
                }

            } else {

                // GET /jobs/5

                int id = Integer.parseInt(pathInfo.substring(1));

                Jobs job = jobService.getJobById(id);

                if (job == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy công việc");
                    return;
                }

                resp.getWriter().println("ID: " + job.getId());
                resp.getWriter().println("Title: " + job.getTitle());
                resp.getWriter().println("Description: " + job.getDescription());
                resp.getWriter().println("Salary: " + job.getSalary());
                resp.getWriter().println("Location: " + job.getLocation());
                resp.getWriter().println("Experience: " + job.getExperience());
                resp.getWriter().println("Quantity: " + job.getQuantity());
            }

        } catch (NumberFormatException e) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ");

        } catch (Exception e) {

            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Cấu hình request và response (Phải đặt ở đầu)
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Map dữ liệu và gọi service xử lý
            JobDTO dto = JobRequestMapper.toDTO(req);
            jobService.addJob(dto);

            // Đặt HTTP Status là 200 OK
            resp.setStatus(HttpServletResponse.SC_OK);

            // Tạo đối tượng ApiResponse thành công
            ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Thêm công việc thành công!", null);

            // Gửi JSON về client bằng Jackson
            String jsonResult = this.objectMapper.writeValueAsString(apiResponse);
            resp.getWriter().print(jsonResult);

        } catch (NumberFormatException e) {
            // Đặt HTTP Status là 400 Bad Request
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            // Tạo đối tượng ApiResponse thất bại
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Dữ liệu số không hợp lệ hoặc bị trống!");

            String jsonResult = this.objectMapper.writeValueAsString(apiResponse);
            resp.getWriter().print(jsonResult);

        } catch (IllegalArgumentException e) {
            // Đặt HTTP Status là 400 Bad Request
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            // Lấy message lỗi từ Exception truyền vào ApiResponse
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, e.getMessage());

            String jsonResult = this.objectMapper.writeValueAsString(apiResponse);
            resp.getWriter().print(jsonResult);

        } catch (Exception e) {
            // Bẫy thêm lỗi hệ thống (500 Internal Server Error) nếu có
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Có lỗi hệ thống xảy ra: " + e.getMessage());

            String jsonResult = this.objectMapper.writeValueAsString(apiResponse);
            resp.getWriter().print(jsonResult);
        } finally {
            // Đảm bảo dữ liệu được đẩy đi hết
            resp.getWriter().flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            JobDTO dto = JobRequestMapper.toDTO(req);
            jobService.updateJob(id, dto);

            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("Update Job Success!");

        } catch (NumberFormatException e) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu số không hợp lệ");

        } catch (java.time.format.DateTimeParseException e) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Định dạng ngày giờ không hợp lệ");

        } catch (IllegalArgumentException e) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            jobService.deleteJob(id);

            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().println("Delete Job Success!");

        } catch (NumberFormatException e) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ");

        } catch (IllegalArgumentException e) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
