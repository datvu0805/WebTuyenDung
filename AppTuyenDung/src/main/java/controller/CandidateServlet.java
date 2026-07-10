package controller;

import com.google.gson.Gson;
import dao.CandicateDAO;
import dao.UserDAO;
import dto.ApiResponse;
import dto.CandidateDTO;
import model.Candidates;
import model.Users;
import service.FileService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/candidate/profile")
public class CandidateServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final CandicateDAO candicateDAO = new CandicateDAO();
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
            resp.getWriter().write(gson.toJson(new ApiResponse<>(false, "Không tìm thấy user", null)));
            return;
        }

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phoneNumber = req.getParameter("phoneNumber");
        String address = req.getParameter("address");
        String dateOfBirth = req.getParameter("dateOfBirth");

        if (fullName != null && !fullName.isBlank()) user.setFullName(fullName);
        if (email != null && !email.isBlank()) user.setEmail(email);
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);
        if (address != null) user.setAddress(address);
        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            user.setDateOfBirth(LocalDate.parse(dateOfBirth));
        }
        Connection conn = null;
        try {
            conn = userDAO.getConnection();
            userDAO.update(conn,userId, user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        resp.getWriter().write(gson.toJson(new ApiResponse<>(true, "Cập nhật thông tin thành công", null)));
    }
}