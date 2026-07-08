package controller;

import com.google.gson.Gson;
import dao.CandicateDAO;
import dto.ApiResponse;
import dto.CandidateDTO;
import model.Candidates;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/candidate/profile")
public class CandidateServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final CandicateDAO candicateDAO = new CandicateDAO();

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
        dto.setAvatarUrl(candidate.getUser().getAvatarUrl());
        dto.setEmail(candidate.getUser().getEmail());
        dto.setPhoneNumber(candidate.getUser().getPhoneNumber());
        dto.setAddress(candidate.getUser().getAddress());

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
}