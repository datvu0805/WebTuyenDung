package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import model.SystemSetting;
import service.SystemSettingService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/settings")
public class AdminSettingsServlet extends BaseServlet {

    private final SystemSettingService systemSettingService = new SystemSettingService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {

            List<SystemSetting> settings = systemSettingService.getAll();

            ApiResponse<List<SystemSetting>> response =
                    new ApiResponse<>(true, "Lấy cấu hình hệ thống thành công", settings);

            resp.getWriter().write(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        try {

            Map<String, String> body = objectMapper.readValue(req.getReader(), Map.class);

            String key = body.get("key");
            String value = body.get("value");

            systemSettingService.setValue(key, value);

            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(true, "Cập nhật cấu hình thành công", null)));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }
}
