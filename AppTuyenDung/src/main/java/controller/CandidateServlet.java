package controller;

import com.google.gson.Gson;
import dao.CandidateDAO;
import dao.UserDAO;
import dto.ApiResponse;
import dto.CandidateDTO;
import dto.CandidateProfileDTO;
import model.Candidates;
import model.Users;
import service.FileService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;

@WebServlet("/candidate/profile")
public class CandidateServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final CandidateDAO candicateDAO = new CandidateDAO();
    private final UserDAO userDAO = new UserDAO();
    private final FileService fileService = new FileService();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        Candidates candidate = candicateDAO.findByUserId(userId);

        if (candidate == null) {
            ApiResponse<Object> result =
                    new ApiResponse<>(false, "Không tìm thấy candidate", null);

            resp.getWriter().write(gson.toJson(result));
            return;
        }

        CandidateDTO dto = new CandidateDTO();

        dto.setCandidateId(candidate.getId());
        dto.setUserId(candidate.getUser().getId());
        dto.setUsername(candidate.getUser().getUsername());
        dto.setFullName(candidate.getUser().getFullName());
        dto.setEmail(candidate.getUser().getEmail());
        dto.setPhoneNumber(candidate.getUser().getPhoneNumber());
        dto.setAddress(candidate.getUser().getAddress());

        // Generate presigned URL nếu avatarUrl là object path
        String avatarUrl = candidate.getUser().getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isBlank() && !avatarUrl.startsWith("http")) {
            try { avatarUrl = fileService.getPresignedUrl(avatarUrl); } catch (Exception ignored) {}
        }
        dto.setAvatarUrl(avatarUrl);

        if (candidate.getUser().getDateOfBirth() != null) {
            dto.setDateOfBirth(candidate.getUser().getDateOfBirth().toString());
        }

        dto.setRole(candidate.getUser().getRole().getRoleName());

        ApiResponse<CandidateDTO> result =
                new ApiResponse<>(
                        true,
                        "Lấy thông tin candidate thành công",
                        dto
                );

        resp.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        Users user = userDAO.getByID(userId);
        if (user == null) {
            resp.getWriter().write(gson.toJson(
                    new ApiResponse<>(false, "Không tìm thấy user", null)
            ));
            return;
        }

        // ✔ chỉ dùng JSON
        CandidateProfileDTO dto = gson.fromJson(req.getReader(), CandidateProfileDTO.class);

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());

        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isBlank()) {
            user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        }

        try (Connection conn = userDAO.getConnection()) {
            userDAO.update(conn, userId, user);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(gson.toJson(
                    new ApiResponse<>(false, "Lỗi update", null)
            ));
            return;
        }

        resp.getWriter().write(gson.toJson(
                new ApiResponse<>(true, "Cập nhật thành công", null)
        ));
    }
}