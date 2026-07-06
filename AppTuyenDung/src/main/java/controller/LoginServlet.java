package controller;

import com.google.gson.Gson;
import model.Users;
import service.AuthService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            Users user = authService.login(username, password);

            if (user == null) {
                result.put("success", false);
                result.put("message", "Sai tài khoản hoặc mật khẩu");
            } else {
                HttpSession session = req.getSession();

                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("role", user.getRole().getRoleName());

                result.put("success", true);
                result.put("message", "Đăng nhập thành công");
                result.put("userId", user.getId());
                result.put("username", user.getUsername());
                result.put("role", user.getRole().getRoleName());
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getClass().getName() + ": " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(result));
    }
}