package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import dto.LoginResponseDTO;
import model.Users;
import service.AuthService;
import service.FileService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();
    private final FileService fileService = new FileService();

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
                data.setEmail(user.getEmail());
                data.setPhoneNumber(user.getPhoneNumber());
                data.setAddress(user.getAddress());
                data.setRole(user.getRole().getRoleName());

                // Generate presigned URL nếu avatarUrl là object path (không phải http)
                String avatarUrl = user.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.isBlank() && !avatarUrl.startsWith("http")) {
                    try {
                        avatarUrl = fileService.getPresignedUrl(avatarUrl);
                    } catch (Exception ignored) {}
                }
                data.setAvatarUrl(avatarUrl);

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