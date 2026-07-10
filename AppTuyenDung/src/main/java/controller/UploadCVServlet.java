package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.UploadCVDTO;
import model.CV;
import service.CVService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/UploadCV")
@MultipartConfig(
        fileSizeThreshold = 0,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 11
)
public class UploadCVServlet extends HttpServlet {
    private final CVService cvService = new CVService();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            UploadCVDTO dto = new UploadCVDTO();
            dto.setCandidateId(request.getParameter("candidate_id"));
            dto.setCvTitle(request.getParameter("cv_title"));
            dto.setDescription(request.getParameter("description"));
            dto.setVersion(request.getParameter("version"));
            dto.setFileCV(request.getPart("file"));
//            dto.setFileAvatar(request.getPart("avatar_url"));

            CV saveCV = cvService.handleUploadCV(dto);

            saveCV.setCreatedAt(null);
            saveCV.setUpdatedAt(null);
            if (saveCV != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                ApiResponse<CV> apiResponse = new ApiResponse<>(true, "Hệ thống đã ẩy file lên MinIO thành công", saveCV);
                response.getWriter().print(this.objectMapper.writeValueAsString(apiResponse));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, e.getMessage());
            response.getWriter().print(this.objectMapper.writeValueAsString(apiResponse));
        }finally {
            response.getWriter().flush();
        }
    }
}