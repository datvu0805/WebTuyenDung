package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import service.CompanyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/company/delete")
public class AdminDeleteCompanyServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final CompanyService companyService = new CompanyService();

    @Override
    protected void doDelete(HttpServletRequest req,
                            HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> result;

        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                result = new ApiResponse<>(
                        false,
                        "Thiếu id công ty",
                        null
                );

                resp.getWriter().write(gson.toJson(result));
                return;
            }

            int id = Integer.parseInt(idParam);

            String error = companyService.deleteCompany(id);

            if (error == null) {
                result = new ApiResponse<>(
                        true,
                        "Xóa công ty thành công",
                        null
                );
            } else {
                result = new ApiResponse<>(
                        false,
                        error,
                        null
                );
            }

        } catch (NumberFormatException e) {
            result = new ApiResponse<>(
                    false,
                    "ID công ty không hợp lệ",
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            result = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            );
        }

        resp.getWriter().write(gson.toJson(result));
    }
}