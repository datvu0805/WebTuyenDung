package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import dto.PaymentCreateResponseDTO;
import dto.PurchaseRequestDTO;
import dto.ServicePackageDTO;
import dto.UserVipStatusDTO;
import exception.BusinessException;
import service.PaymentService;
import validator.PaymentValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {
        "/api/payment/packages",
        "/api/payment/vip-status",
        "/api/payment/create",
        "/api/payment/transaction-status",
        "/api/payment/history"
})
public class PurchasePackageServlet extends HttpServlet {
    private final PaymentService paymentService = new PaymentService();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                write(response, HttpServletResponse.SC_UNAUTHORIZED, new ApiResponse<>(false, "Bạn chưa đăng nhập"));
                return;
            }
            Integer userId = (Integer) session.getAttribute("userId");
            String role = (String) session.getAttribute("role");

            if ("/api/payment/packages".equals(path)) {
                List<ServicePackageDTO> packages = paymentService.listPackages(role);
                write(response, 200, new ApiResponse<>(true, "Danh sách gói VIP", packages));
                return;
            }
            if ("/api/payment/vip-status".equals(path)) {
                UserVipStatusDTO status = paymentService.getVipStatus(userId);
                write(response, 200, new ApiResponse<>(true, "Trạng thái VIP", status));
                return;
            }
            if ("/api/payment/history".equals(path)) {
                if (!"CANDIDATE".equalsIgnoreCase(role) && !"EMPLOYER".equalsIgnoreCase(role)) {
                    write(response, HttpServletResponse.SC_FORBIDDEN,
                            new ApiResponse<>(false, "Vai trò này không có lịch sử thanh toán"));
                    return;
                }
                write(response, 200, new ApiResponse<>(true, "Lịch sử thanh toán", paymentService.getTransactionHistory(userId)));
                return;
            }
            write(response, 404, new ApiResponse<>(false, "API không tồn tại"));
        } catch (Exception e) {
            writeError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                write(response, HttpServletResponse.SC_UNAUTHORIZED, new ApiResponse<>(false, "Bạn chưa đăng nhập"));
                return;
            }
            Integer userId = (Integer) session.getAttribute("userId");
            String role = (String) session.getAttribute("role");

            if ("/api/payment/create".equals(path)) {
                PurchaseRequestDTO requestDTO = objectMapper.readValue(request.getReader(), PurchaseRequestDTO.class);
                // Luôn lấy userId từ session — chống IDOR
                requestDTO.setUserID(userId);
                PaymentValidator.vadidatePurchaseInput(requestDTO);

                PaymentCreateResponseDTO data = paymentService.createPayment(
                        userId, role, requestDTO.getPackageID());
                write(response, 200, new ApiResponse<>(true, "Tạo phiên thanh toán thành công", data));
                return;
            }
            write(response, 404, new ApiResponse<>(false, "API không tồn tại"));
        } catch (Exception e) {
            writeError(response, e);
        }
    }

    private void write(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private void writeError(HttpServletResponse response, Exception e) throws IOException {
        if (e instanceof BusinessException.ValidationException) {
            write(response, 400, new ApiResponse<>(false, e.getMessage()));
            return;
        }
        if (e instanceof BusinessException) {
            write(response, 400, new ApiResponse<>(false, e.getMessage()));
            return;
        }
        e.printStackTrace();
        write(response, 500, new ApiResponse<>(false, "Lỗi hệ thống: " + e.getMessage()));
    }
}
