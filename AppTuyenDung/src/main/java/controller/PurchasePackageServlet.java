package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.PurchaseRequestDTO;
import service.PaymentService;
import validator.PaymentValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/payment/purchase")
public class PurchasePackageServlet extends HttpServlet {
    private final PaymentService paymentService = new PaymentService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            // Đọc luồng dữ liệu JSON từ request body map trực tiếp vào đối tượng DTO vận chuyển qua Jackson
            PurchaseRequestDTO requestDTO = objectMapper.readValue(request.getReader(), PurchaseRequestDTO.class);

            // 1. Chạy qua bộ kiểm định dữ liệu đầu vào định dạng số
            PaymentValidator.vadidatePurchaseInput(requestDTO);

            // 2. Chuyển giao luồng dữ liệu xuống tầng nghiệp vụ điều phối giao dịch mua gói Premium
            paymentService.purchasepackage(requestDTO.getUserID(), requestDTO.getPackageID());

            // 3. Phản hồi cấu trúc đối tượng dữ liệu JSON thành công tiêu chuẩn về phía Client
            response.setStatus(HttpServletResponse.SC_OK);
            ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Kích hoạt tính năng Premium Boost thành công! Tài khoản của bạn đã được nâng cấp đặc quyền.");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        } catch (Exception e) {
            throw new ServletException(e); // Quăng ngoại lệ lên bộ lọc lỗi tập trung ngoài cùng (Filter) xử lý kết xuất
        }
    }
}
