package controller;

import dto.UploadCVDTO;
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
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB bộ đệm
        maxFileSize = 1024 * 1024 * 10,       // 10MB tối đa cho 1 file
        maxRequestSize = 1024 * 1024 * 30     // 30MB trần tổng request
)
public class UploadCVServlet extends HttpServlet {
    private final CVService cvService = new CVService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        try {
            UploadCVDTO dto = new UploadCVDTO();
            dto.setCandidateId(request.getParameter("candidate_id"));
            dto.setCvTitle(request.getParameter("cv_title"));
            dto.setDescription(request.getParameter("description"));
            dto.setVersion(request.getParameter("version"));
            dto.setFileCV(request.getPart("file"));
            dto.setFileAvatar(request.getPart("avatar_url"));

            String result = cvService.handleUploadCV(dto);

            if ("SUCCESS".equals(result)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Thành công: Hệ thống phân lớp đã lưu hồ sơ hoàn chỉnh!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Lỗi hệ thống máy chủ Servlet: " + e.getMessage());
        }
    }
}