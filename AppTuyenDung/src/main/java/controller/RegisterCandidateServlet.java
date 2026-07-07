package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import dto.RegisterCandidateDTO;
import service.AuthService;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register-candidate")
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
public class RegisterCandidateServlet extends BaseServlet {

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
            RegisterCandidateDTO dto = new RegisterCandidateDTO();

            dto.setUsername(req.getParameter("username"));
            dto.setPassword(req.getParameter("password"));
            dto.setFullName(req.getParameter("fullName"));
            dto.setEmail(req.getParameter("email"));
            dto.setPhoneNumber(req.getParameter("phoneNumber"));
            dto.setAddress(req.getParameter("address"));

            Part avatar = req.getPart("avatar");

            String error = authService.registerCandidate(dto, avatar);

            if (error == null) {
                result = new ApiResponse<>(true, "Đăng ký candidate thành công", null);
            } else {
                result = new ApiResponse<>(false, error, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = new ApiResponse<>(false, e.getMessage(), null);
        }

        resp.getWriter().write(gson.toJson(result));
    }
}