package controller;

import com.google.gson.Gson;
import model.Users;
import service.AuthService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register-candidate")
public class RegisterCandidateServlet extends BaseServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            AuthService authService = new AuthService();

            Users user = new Users();
            user.setUsername(req.getParameter("username"));
            user.setPassword(req.getParameter("password"));
            user.setFullName(req.getParameter("fullName"));
            user.setAvatarUrl(req.getParameter("avatarUrl"));
            user.setEmail(req.getParameter("email"));
            user.setPhoneNumber(req.getParameter("phoneNumber"));
            user.setAddress(req.getParameter("address"));

            String error = authService.registerCandidate(user);

            if (error != null) {
                result.put("success", false);
                result.put("message", error);
            } else {
                result.put("success", true);
                result.put("message", "Đăng ký candidate thành công");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getClass().getName() + ": " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(result));
    }
}