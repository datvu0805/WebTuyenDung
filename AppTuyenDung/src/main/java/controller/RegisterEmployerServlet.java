package controller;

import com.google.gson.Gson;
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
public class RegisterEmployerServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        // ===== User =====
        Users user = new Users();
        user.setUsername(req.getParameter("username"));
        user.setPassword(req.getParameter("password"));
        user.setFullName(req.getParameter("fullName"));
        user.setAvatarUrl(req.getParameter("avatarUrl"));
        user.setEmail(req.getParameter("email"));
        user.setPhoneNumber(req.getParameter("phoneNumber"));
        user.setAddress(req.getParameter("address"));

        // ===== Company =====
        Company company = new Company();
        company.setCompanyName(req.getParameter("companyName"));
        company.setDescription(req.getParameter("description"));

        String error = null;
        try {
            error = authService.registerEmployer(user, company);
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