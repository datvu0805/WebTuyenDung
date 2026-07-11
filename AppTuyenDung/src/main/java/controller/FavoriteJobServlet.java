package controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dto.ApiResponse;
import dto.FavoriteJobDTO;

import model.Candidates;
import model.Job;

import service.FavoriteJobService;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet(urlPatterns = {"/favorite-jobs/*"})
public class FavoriteJobServlet extends BaseServlet {


    private final FavoriteJobService favoriteJobService = new FavoriteJobService();


    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");


        try {


            String pathInfo = req.getPathInfo();


            // GET /favorite-jobs/candidate?id=1
            if ("/candidate".equals(pathInfo)) {


                int candidateId = Integer.parseInt(req.getParameter("id"));


                List<Job> jobs = favoriteJobService.getFavoriteJobs(candidateId);


                ApiResponse<List<Job>> response = new ApiResponse<>(true, "Lấy danh sách công việc yêu thích thành công!", jobs);


                resp.setStatus(HttpServletResponse.SC_OK);


                resp.getWriter().print(objectMapper.writeValueAsString(response));


                return;
            }


            // GET /favorite-jobs/job?id=1
            if ("/job".equals(pathInfo)) {


                int jobId = Integer.parseInt(req.getParameter("id"));


                List<Candidates> candidates = favoriteJobService.getCandidatesFavoriteJob(jobId);


                ApiResponse<List<Candidates>> response = new ApiResponse<>(true, "Lấy danh sách ứng viên yêu thích công việc thành công!", candidates);


                resp.setStatus(HttpServletResponse.SC_OK);


                resp.getWriter().print(objectMapper.writeValueAsString(response));


                return;
            }


            // GET /favorite-jobs/check?candidateId=1&jobId=2
            if ("/check".equals(pathInfo)) {


                int candidateId = Integer.parseInt(req.getParameter("candidateId"));


                int jobId = Integer.parseInt(req.getParameter("jobId"));


                boolean exists = favoriteJobService.exists(candidateId, jobId);


                ApiResponse<Boolean> response = new ApiResponse<>(true, "Kiểm tra trạng thái yêu thích thành công!", exists);


                resp.setStatus(HttpServletResponse.SC_OK);


                resp.getWriter().print(objectMapper.writeValueAsString(response));


                return;
            }


            // GET /favorite-jobs/count?jobId=1
            if ("/count".equals(pathInfo)) {


                int jobId = Integer.parseInt(req.getParameter("jobId"));


                int count = favoriteJobService.countFavoriteByJobId(jobId);


                ApiResponse<Integer> response = new ApiResponse<>(true, "Lấy số lượt yêu thích thành công!", count);


                resp.setStatus(HttpServletResponse.SC_OK);


                resp.getWriter().print(objectMapper.writeValueAsString(response));


                return;
            }


            // Không khớp URL
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


            FavoriteJobDTO dto = objectMapper.readValue(req.getReader(), FavoriteJobDTO.class);


            favoriteJobService.add(dto);


            ApiResponse<Object> response = new ApiResponse<>(true, "Thêm công việc vào yêu thích thành công!", null);


            resp.setStatus(HttpServletResponse.SC_CREATED);


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


            FavoriteJobDTO dto = objectMapper.readValue(req.getReader(), FavoriteJobDTO.class);


            favoriteJobService.delete(dto);


            ApiResponse<Object> response = new ApiResponse<>(true, "Xóa công việc khỏi danh sách yêu thích thành công!", null);


            resp.setStatus(HttpServletResponse.SC_OK);


            resp.getWriter().print(objectMapper.writeValueAsString(response));


        } catch (IllegalArgumentException e) {


            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);


            resp.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, e.getMessage(), null)));


        } catch (Exception e) {


            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


            resp.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, e.getMessage(), null)));

        }
    }
}
