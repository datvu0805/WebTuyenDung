package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import service.CVService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/XoaCV")
public class DeleteCVServlet extends HttpServlet {
    private final CVService cvService = new CVService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try{
            String cvIdParam = request.getParameter("cv_id");

            if (cvIdParam == null || cvIdParam.trim().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, "Thiếu tham số cv_id")));
                return;
            }

            int cvId = Integer.parseInt(cvIdParam);

            String candidateParam = request.getParameter("candidate_id");
            if(candidateParam == null || candidateParam.trim().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, "Thiếu candidate_id")));
                return;
            }
            int candidateID = Integer.parseInt(candidateParam);
            boolean sucess = cvService.hanleDeleteCV(cvId, candidateID);

            if (sucess){
                response.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(true, "Đã xóa CV thành công!")));
            }else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, "Xóa CV thất bại!")));
            }
        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, "Lỗi hệ thống: " +e.getMessage())));
        }
    }
}
