package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import dto.JobCertificateBatchDTO;
import dto.JobCertificateDTO;
import dto.JobCertificateRequirementDTO;
import mapper.JobCertificateBatchRequestMapper;
import mapper.JobCertificateRequestMapper;
import model.Job;
import service.JobCertificateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/job-certificates/*"})
public class JobCertificateServlet extends BaseServlet {

    private final JobCertificateService jobCertificateService = new JobCertificateService();

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /job-certificates/job?id=1 — trả kèm điểm/tiêu chuẩn tối thiểu yêu cầu (requiredScore)
            if ("/job".equals(pathInfo)) {

                int jobId = Integer.parseInt(req.getParameter("id"));

                List<JobCertificateRequirementDTO> requirements = jobCertificateService.getRequirementsByJob(jobId);

                ApiResponse<List<JobCertificateRequirementDTO>> response =
                        new ApiResponse<>(true, "Lấy danh sách chứng chỉ thành công!", requirements);

                resp.setStatus(HttpServletResponse.SC_OK);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }

            // GET /job-certificates/certificate?id=1
            if ("/certificate".equals(pathInfo)) {

                int certificateId = Integer.parseInt(req.getParameter("id"));

                List<Job> jobs = jobCertificateService.getJobsByCertificate(certificateId);

                ApiResponse<List<Job>> response = new ApiResponse<>(true, "Lấy danh sách công việc thành công!", jobs);

                resp.setStatus(HttpServletResponse.SC_OK);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

                return;
            }

            ApiResponse<Object> response = new ApiResponse<>(false, "Đường dẫn không hợp lệ", null);

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");


        try {

            String pathInfo = req.getPathInfo();


            if (pathInfo == null || "/".equals(pathInfo)) {


                JobCertificateBatchDTO dto = objectMapper.readValue(req.getReader(), JobCertificateBatchDTO.class);


                jobCertificateService.add(dto);


                ApiResponse<Object> response = new ApiResponse<>(true, "Thêm chứng chỉ cho công việc thành công!", null);


                resp.setStatus(HttpServletResponse.SC_CREATED);


                resp.getWriter().print(objectMapper.writeValueAsString(response));


                return;
            }


            if ("/single".equals(pathInfo)) {


                JobCertificateDTO dto = objectMapper.readValue(req.getReader(), JobCertificateDTO.class);


                jobCertificateService.add(dto);


                ApiResponse<Object> response = new ApiResponse<>(true, "Thêm chứng chỉ thành công!", null);


                resp.setStatus(HttpServletResponse.SC_CREATED);


                resp.getWriter().print(objectMapper.writeValueAsString(response));


                return;
            }


            ApiResponse<Object> response = new ApiResponse<>(false, "Đường dẫn không hợp lệ", null);


            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);


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


        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");


        try {


            JobCertificateDTO dto = objectMapper.readValue(req.getReader(), JobCertificateDTO.class);


            jobCertificateService.delete(dto);


            ApiResponse<Object> response = new ApiResponse<>(true, "Xóa chứng chỉ khỏi công việc thành công!", null);


            resp.setStatus(HttpServletResponse.SC_OK);


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