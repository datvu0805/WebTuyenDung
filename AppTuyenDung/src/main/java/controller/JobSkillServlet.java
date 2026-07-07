package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import model.JobSkills;
import model.Jobs;
import model.Skills;
import service.JobSkillService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/job-skills/*")
public class JobSkillServlet extends BaseServlet {

    private final JobSkillService jobSkillService = new JobSkillService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            int jobId = Integer.parseInt(req.getParameter("jobId"));
            int skillId = Integer.parseInt(req.getParameter("skillId"));

            JobSkills jobSkill = new JobSkills();
            jobSkill.setJobID(new Jobs(jobId));
            jobSkill.setSkillID(new Skills(skillId));

            jobSkillService.add(jobSkill);

            resp.setStatus(HttpServletResponse.SC_OK);

            ApiResponse<JobSkills> response = new ApiResponse<>(true, "Thêm skill cho job thành công!", jobSkill);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<JobSkills> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            int jobId = Integer.parseInt(req.getParameter("jobId"));
            int skillId = Integer.parseInt(req.getParameter("skillId"));

            JobSkills jobSkill = new JobSkills();
            jobSkill.setJobID(new Jobs(jobId));
            jobSkill.setSkillID(new Skills(skillId));

            jobSkillService.delete(jobSkill);

            ApiResponse<JobSkills> response = new ApiResponse<>(true, "Xóa skill khỏi job thành công!", jobSkill);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<JobSkills> response = new ApiResponse<>(false, e.getMessage(), null);

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

            if (jobId != null) {

                List<JobSkills> list = jobSkillService.getByJobId(Integer.parseInt(jobId));

                ApiResponse<List<JobSkills>> response = new ApiResponse<>(true, "Danh sách skill của job", list);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

            } else if (skillId != null) {

                List<JobSkills> list = jobSkillService.getBySkillId(Integer.parseInt(skillId));

                ApiResponse<List<JobSkills>> response = new ApiResponse<>(true, "Danh sách job theo skill", list);

                resp.getWriter().print(objectMapper.writeValueAsString(response));

            } else {

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                ApiResponse<Object> response = new ApiResponse<>(false, "Thiếu jobId hoặc skillId", null);

                resp.getWriter().print(objectMapper.writeValueAsString(response));
            }

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }
}