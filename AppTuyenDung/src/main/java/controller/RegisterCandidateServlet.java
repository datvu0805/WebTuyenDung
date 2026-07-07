package controller;

import com.google.gson.Gson;
import dto.RegisterCandidateDTO;

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
        RegisterCandidateDTO dto = new RegisterCandidateDTO();

        try {
            AuthService authService = new AuthService();

            
            dto.setUsername(req.getParameter("username"));
            dto.setPassword(req.getParameter("password"));
            dto.setFullName(req.getParameter("fullName"));
            dto.setAvatarUrl(req.getParameter("avatarUrl"));
            dto.setEmail(req.getParameter("email"));
            dto.setPhoneNumber(req.getParameter("phoneNumber"));
            dto.setAddress(req.getParameter("address"));

            String error = authService.registerCandidate(dto);

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