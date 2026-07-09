package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import dto.CandidateProfileDTO;
import service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/candidate/profile/update")
public class UpdateProfileCandidateServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AuthService candidateService = new AuthService();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        ApiResponse<?> result;

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result = new ApiResponse<>(false, "Bạn chưa đăng nhập", null);
                response.getWriter().println(gson.toJson(result));
                return;
            }

            int userId = (Integer) session.getAttribute("userId");

            CandidateProfileDTO dto =
                    gson.fromJson(request.getReader(), CandidateProfileDTO.class);

            if (dto == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result = new ApiResponse<>(false, "Dữ liệu gửi lên không hợp lệ", null);
                response.getWriter().println(gson.toJson(result));
                return;
            }

            String error = candidateService.updateCandidateProfile(userId, dto);

            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result = new ApiResponse<>(false, error, null);
            } else {
                result = new ApiResponse<>(true, "Cập nhật thành công", null);
            }

        } catch (Exception e) {
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result = new ApiResponse<>(false, e.getMessage(), null);
        }

        response.getWriter().println(gson.toJson(result));
    }
}