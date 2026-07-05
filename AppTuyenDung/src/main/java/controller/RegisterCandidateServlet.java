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
public class RegisterCandidateServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String avatarUrl = req.getParameter("avatarUrl");
        String email = req.getParameter("email");
        String phoneNumber = req.getParameter("phoneNumber");
        String address = req.getParameter("address");

        Users user = new Users();

        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);

        String error = authService.registerCandidate(user);

        Map<String, Object> result = new HashMap<>();

        if (error != null) {
            result.put("success", false);
            result.put("message", error);
        } else {
            result.put("success", true);
            result.put("message", "Đăng ký candidate thành công");
        }

        resp.getWriter().write(gson.toJson(result));
    }
}