package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import dto.CompanyResponseDTO;
import service.CompanyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/company/list")
public class AdminListCompanyServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final CompanyService companyService = new CompanyService();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            List<CompanyResponseDTO> companies =
                    companyService.getAllCompanies();

            ApiResponse<List<CompanyResponseDTO>> result =
                    new ApiResponse<>(
                            true,
                            "Lấy danh sách công ty thành công",
                            companies
                    );

            resp.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            ApiResponse<?> result =
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    );

            resp.getWriter().write(gson.toJson(result));
        }
    }
}