package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import dto.LoginResponseDTO;
import model.Users;
import service.AuthService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

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

        ApiResponse<?> result;

        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            Users user = authService.login(username, password);

            if (user == null) {
                result = new ApiResponse<>(
                        false,
                        "Sai tài khoản hoặc mật khẩu",
                        null
                );
            } else {
                HttpSession session = req.getSession();

                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("role", user.getRole().getRoleName());

                LoginResponseDTO data = new LoginResponseDTO();

                data.setUserId(user.getId());
                data.setUsername(user.getUsername());
                data.setFullName(user.getFullName());
                data.setAvatarUrl(user.getAvatarUrl());
                data.setEmail(user.getEmail());
                data.setPhoneNumber(user.getPhoneNumber());
                data.setAddress(user.getAddress());
                data.setRole(user.getRole().getRoleName());

                result = new ApiResponse<>(
                        true,
                        "Đăng nhập thành công",
                        data
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            result = new ApiResponse<>(
                    false,
                    e.getClass().getName() + ": " + e.getMessage(),
                    null
            );
        }

        resp.getWriter().write(gson.toJson(result));
    }
}