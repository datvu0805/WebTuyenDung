package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import dto.JobDTO;
import dto.JobSearchDTO;
import dto.PageResponse;
import mapper.JobRequestMapper;
import mapper.JobSearchRequestMapper;
import service.JobService;
import service.RecommendationService;
import service.PaymentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/jobs/*"})
public class JobSevrlet extends BaseServlet {
    private final JobService jobService = new JobService();
    private final RecommendationService recommendationService = new RecommendationService();
    private final PaymentService paymentService = new PaymentService();
    // chuyển đổi để json đọc được data từ kiểu localdatetime
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();


            // GET /jobs/search
            if ("/search".equals(pathInfo)) {

                JobSearchDTO searchDTO = JobSearchRequestMapper.toDTO(req);

                PageResponse<JobDTO> jobs = jobService.search(searchDTO);

                ApiResponse<PageResponse<JobDTO>> response =
                        new ApiResponse<>(true, "Tìm kiếm thành công", jobs);

                resp.setStatus(HttpServletResponse.SC_OK);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }

            // GET /jobs/recommended?limit=10 — gợi ý việc làm cho ứng viên đang đăng nhập
            if ("/recommended".equals(pathInfo)) {

                HttpSession session = req.getSession(false);
                Integer candidateId = session == null ? null : (Integer) session.getAttribute("candidateId");
                Integer userId = session == null ? null : (Integer) session.getAttribute("userId");

                if (candidateId == null || userId == null) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().print(objectMapper.writeValueAsString(
                            new ApiResponse<>(false, "Chỉ ứng viên mới có thể xem gợi ý việc làm.", null)));
                    return;
                }

                if (!paymentService.getVipStatus(userId).isActive()) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().print(objectMapper.writeValueAsString(
                            new ApiResponse<>(false, "Vui lòng nạp VIP để sử dụng AI gợi ý việc làm.", null)));
                    return;
                }

                String limitParam = req.getParameter("limit");
                int limit = 10;
                if (limitParam != null && !limitParam.isBlank()) {
                    limit = Integer.parseInt(limitParam);
                }

                List<JobDTO> jobs = recommendationService.recommend(candidateId, limit);

                ApiResponse<List<JobDTO>> response =
                        new ApiResponse<>(true, "Lấy gợi ý việc làm thành công", jobs);

                resp.setStatus(HttpServletResponse.SC_OK);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }

            // GET /jobs/company?companyId=X — jobs theo công ty
            if ("/company".equals(pathInfo)) {
                String companyIdParam = req.getParameter("companyId");
                if (companyIdParam == null || companyIdParam.isBlank()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, "Thiếu companyId", null)));
                    return;
                }
                JobSearchDTO dto = new JobSearchDTO();
                dto.setCompanyId(Integer.parseInt(companyIdParam));
                dto.setPage(1);
                dto.setSize(50);
                PageResponse<JobDTO> jobs = jobService.search(dto);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(true, "Lấy jobs theo công ty thành công", jobs)));
                return;
            }


            // GET /jobs hoặc GET /jobs/
            if (pathInfo == null || "/".equals(pathInfo)) {

                List<JobDTO> jobs = jobService.getAllJobs();

                ApiResponse<List<JobDTO>> response = new ApiResponse<>(true, "Lấy danh sách công việc thành công!", jobs);

                resp.setStatus(HttpServletResponse.SC_OK);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }


            // GET /jobs/{id}
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

//            HttpSession session = req.getSession(false);
//
//            Integer employerId = (Integer) session.getAttribute("employerId");
//
//            dto.setEmployerId(employerId);

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
