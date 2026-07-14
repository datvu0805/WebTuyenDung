package controller;

import com.google.gson.Gson;
import dto.AdminStatisticDTO;
import dto.ApiResponse;
import service.AdminStatisticService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/statistic")
public class AdminStatisticServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AdminStatisticService statisticService =
            new AdminStatisticService();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            boolean cacheHit = statisticService.isCached(); // kiểm tra trước

            AdminStatisticDTO data = statisticService.getStatistic(); // sau đó mới lấy dữ liệu

            resp.setHeader("X-Cache", cacheHit ? "HIT" : "MISS");

            resp.setStatus(HttpServletResponse.SC_OK);

            resp.getWriter().write(gson.toJson(
                    new ApiResponse<>(
                            true,
                            "Lấy thống kê thành công",
                            data
                    )
            ));



        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.getWriter().write(gson.toJson(
                    new ApiResponse<>(
                            false,
                            e.getClass().getSimpleName()
                                    + ": "
                                    + e.getMessage(),
                            null
                    )
            ));
        }
    }
}