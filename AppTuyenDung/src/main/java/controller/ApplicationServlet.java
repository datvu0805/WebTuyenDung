package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.ApplicationDAO;
import dto.ApiResponse;
import dto.ApplicationDTO;
import exception.BusinessException;
import model.Application;
import model.CV;
import model.Candidates;
import model.Job;
import service.ApplicationService;
import validator.ApplicationValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/aplication")
@MultipartConfig
public class ApplicationServlet extends HttpServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private void sendJsonResponse(HttpServletResponse response, int statusCode, ApiResponse<?> apiResponse)
        throws IOException{
        if(!response.isCommitted()){
            response.resetBuffer();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(this.objectMapper.writeValueAsString(apiResponse));
        out.flush();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action =  req.getParameter("action");
        if("viewList".equals(action)){
            try {
                int recruiteriD = (int) req.getSession().getAttribute("userId");
                List<ApplicationDTO> dtoList = applicationService.getRecruiteDashboard(recruiteriD);
                ApiResponse<List<ApplicationDTO>> apiResponse = new ApiResponse<>(true, "Lấy ra danh sách đơn hnagf thành công", dtoList);
                sendJsonResponse(resp, HttpServletResponse.SC_OK, apiResponse);
            }catch (Exception e){
                ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Loi: " +e.getMessage());
                sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, apiResponse);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if("updateStatus".equals(action)){
                String idRaw = req.getParameter("id");
                String statusRaw = req.getParameter("status");

                // nhà tuyển dụng cập nhật th buộc pahir kiểm tra id đơn ứng tuyển
                ApplicationValidator.validateUpdateStatus(idRaw, statusRaw);

                int appID = Integer.parseInt(idRaw);
                int status = Integer.parseInt(statusRaw);
                // 0: chờ duyêt, 1:Phỏng vấn, 2: Đat, 3: Loại

                // cập nhật trạng thái trên DB
                applicationService.updateStatus(appID, status);

                ApplicationDTO dto = new ApplicationDTO();
                try {
                    Application updateApp = applicationDAO.getByID(appID);

                    if (updateApp != null) {
                        dto.setApplicationID(updateApp);
                        dto.setStatus(updateApp.getStatus());
                        dto.setApplieAt(updateApp.getAppliedAt());

                        // thông tin của ứng viên
                        int candidateID = updateApp.getCandidateID().getId();
                        Candidates candidates = new Candidates();
                        candidates.setId(candidateID);
                        dto.setCandidateName(candidates);


                        int cvId = updateApp.getCvID().getId();
                        int jobID = updateApp.getJodID().getId();
                        dto.setJobTitle("ID Công việc: " +jobID);
                        dto.setCvTitle("CV Id: " + cvId);
                    }
                }catch (Exception e){
                    dto.setStatus(status);
                    dto.setApplieAt(LocalDateTime.now());
                }
                        sendJsonResponse(resp, HttpServletResponse.SC_OK, new ApiResponse<>(true, "Cập nhật trạng thái thành công!"));
            }



            else if ("submit".equals(action)){
                String candidateIDRaw = req.getParameter("candidateID");
                String jobIDRaw = req.getParameter("jobID");
                String cvIDRaw = req.getParameter("cvID");
                String coverLetter = req.getParameter("coverLetter");
                String description = req.getParameter("description");

                // chỉ validate thông tin id của các đối tg từ client
                ApplicationValidator.validateSubmit(candidateIDRaw, jobIDRaw, cvIDRaw, coverLetter);

                Application app = new Application();
                Candidates candidates = new Candidates();
                candidates.setId(Integer.parseInt(candidateIDRaw));
                app.setCandidateID(candidates);


                Job jobs = new Job();
                jobs.setId(Integer.parseInt(jobIDRaw));
                app.setJodID(jobs);

                CV cv = new CV();
                cv.setId(Integer.parseInt(cvIDRaw));
                app.setCvID(cv);

                app.setCoverLetter(coverLetter);
                app.setDescription(description);

                applicationService.submitApplication(app);
                if(!resp.isCommitted()){
                    resp.resetBuffer();
                }

                ApplicationDTO dto = applicationDAO.getApplicationDtoById(app.getId());


                sendJsonResponse(resp, HttpServletResponse.SC_CREATED, new ApiResponse<>(true, "Nộp đơn ứng tuyển thành công!", dto));

            }
        }catch (BusinessException e){
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse<>(false, e.getMessage()));

        }catch (Exception e){
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiResponse<>(false, "Lỗi máy chủ: " + e.getMessage()));
        }
    }
}
