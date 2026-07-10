package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import dto.CreateCompanyDTO;
import service.CompanyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/company/create")
public class AdminCreateCompanyServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final CompanyService companyService = new CompanyService();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> result;

        try {
            CreateCompanyDTO dto =
                    gson.fromJson(req.getReader(), CreateCompanyDTO.class);

            String error = companyService.createCompany(dto);

            if (error == null) {
                result = new ApiResponse<>(
                        true,
                        "Tạo công ty thành công",
                        null
                );
            } else {
                result = new ApiResponse<>(
                        false,
                        error,
                        null
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            result = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            );
        }

        resp.getWriter().write(gson.toJson(result));
    }
}