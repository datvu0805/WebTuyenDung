package controller;

import com.google.gson.Gson;
import dao.EmployerDAO;
import dao.UserDAO;
import dto.ApiResponse;
import dto.CandidateDTO;
import model.Employers;
import model.Users;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/employer/profile")
public class EmployerServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final EmployerDAO employerDAO = new EmployerDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        Employers employer = employerDAO.findByUserId(userId);
        if (employer == null) {
            resp.getWriter().write(gson.toJson(new ApiResponse<>(false, "Không tìm thấy employer", null)));
            return;
        }

        CandidateDTO dto = new CandidateDTO();
        dto.setUserId(employer.getUser().getId());
        dto.setUsername(employer.getUser().getUsername());
        dto.setFullName(employer.getUser().getFullName());
        dto.setAvatarUrl(employer.getUser().getAvatarUrl());
        dto.setEmail(employer.getUser().getEmail());
        dto.setPhoneNumber(employer.getUser().getPhoneNumber());
        dto.setAddress(employer.getUser().getAddress());
        if (employer.getUser().getDateOfBirth() != null) {
            dto.setDateOfBirth(employer.getUser().getDateOfBirth().toString());
        }
        dto.setRole(employer.getUser().getRole().getRoleName());

        resp.getWriter().write(gson.toJson(new ApiResponse<>(true, "Lấy thông tin employer thành công", dto)));
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

        userDAO.update(user);

        resp.getWriter().write(gson.toJson(new ApiResponse<>(true, "Cập nhật thông tin thành công", null)));
    }
}
