package controller;

import com.google.gson.Gson;
import dao.UserDAO;
import dto.ApiResponse;
import model.Users;
import service.FileService;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/user/avatar")
@MultipartConfig(
    fileSizeThreshold = 0,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 6
)
public class AvatarServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final UserDAO userDAO = new UserDAO();
    private final FileService fileService = new FileService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        try {
            Part filePart = req.getPart("avatar");
            if (filePart == null || filePart.getSize() == 0) {
                resp.getWriter().write(gson.toJson(new ApiResponse<>(false, "Vui lòng chọn ảnh", null)));
                return;
            }

            String avatarUrl = fileService.uploadImage(filePart, "avatars", userId);

            Users user = userDAO.getByID(userId);
            user.setAvatarUrl(avatarUrl);
            userDAO.update(user.getId(), user);

            resp.getWriter().write(gson.toJson(new ApiResponse<>(true, "Cập nhật ảnh đại diện thành công", avatarUrl)));

        } catch (Exception e) {
            resp.getWriter().write(gson.toJson(new ApiResponse<>(false, e.getMessage(), null)));
        }
    }
}
