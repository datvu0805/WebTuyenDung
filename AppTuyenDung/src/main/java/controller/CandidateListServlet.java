package controller;

import com.google.gson.Gson;
import dao.CandicateDAO;
import dto.ApiResponse;
import dto.CandidateDTO;
import model.Candidates;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/candidate/list")
public class CandidateListServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final CandicateDAO candicateDAO = new CandicateDAO();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        List<Candidates> candidates = candicateDAO.findAll();

        List<CandidateDTO> data = new ArrayList<>();

        for (Candidates candidate : candidates) {
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

            data.add(dto);
        }

        ApiResponse<List<CandidateDTO>> result =
                new ApiResponse<>(
                        true,
                        "Lấy danh sách candidate thành công",
                        data
                );

        resp.getWriter().write(gson.toJson(result));
    }
}