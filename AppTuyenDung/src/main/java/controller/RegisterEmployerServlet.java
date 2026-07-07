package controller;

import com.google.gson.Gson;
import dto.RegisterEmployerDTO;
import model.Company;
import model.Users;
import service.AuthService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register-employer")
public class RegisterEmployerServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();



    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        RegisterEmployerDTO dto = new RegisterEmployerDTO();
        
        // ===== dto =====

        dto.setUsername(req.getParameter("username"));
        dto.setPassword(req.getParameter("password"));
        dto.setFullName(req.getParameter("fullName"));
        dto.setAvatarUrl(req.getParameter("avatarUrl"));
        dto.setEmail(req.getParameter("email"));
        dto.setPhoneNumber(req.getParameter("phoneNumber"));
        dto.setAddress(req.getParameter("address"));

        // ===== Company =====

        dto.setCompanyName(req.getParameter("companyName"));
        dto.setDescription(req.getParameter("description"));

        String error = null;
        try {
            error = authService.registerEmployer(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> result = new HashMap<>();

        if (error == null) {
            result.put("success", true);
            result.put("message", "Đăng ký employer thành công");
        } else {
            result.put("success", false);
            result.put("message", error);
        }

        resp.getWriter().write(gson.toJson(result));
    }
}