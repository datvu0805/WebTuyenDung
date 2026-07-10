package controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import service.CVService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

// quản lý cv
@WebServlet("/quanLyCV")
public class PureDashboardServlet extends HttpServlet {
    private final CVService cvService = new CVService();
    // Thêm các hàm .registerModule để Jackson hiểu được kiểu dữ liệu LocalDateTime
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()) // Thêm dòng này
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // Thêm dòng này (để hiển thị dạng chuỗi đẹp thay vì mảng số)
            .setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       response.setContentType("application/json");
       response.setCharacterEncoding("UTF-8");

       try {
           String idParam = request.getParameter("candidate_id");
           if(idParam == null || idParam.trim().isEmpty()){
               response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               response.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, "Thiếu tham số candidate_id!")));
               return;
           }

           int cadidateId = Integer.parseInt(idParam);

           Map<String, Object> data = cvService.getPureDashboarData(cadidateId);

           ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(true, "Tải dữ liệu thành công!", data);
           response.getWriter().print(objectMapper.writeValueAsString(apiResponse));

       }catch (Exception e){
           e.printStackTrace();
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           response.getWriter().print(objectMapper.writeValueAsString(new ApiResponse<>(false, "Gặp lỗi hệ thống: " +e.getMessage())));
       }
    }
}
