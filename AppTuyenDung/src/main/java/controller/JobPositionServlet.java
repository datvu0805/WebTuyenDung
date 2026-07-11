package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.JobPositionDAO;
import dto.ApiResponse;
import model.JobPosition;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/job-positions/*")
public class JobPositionServlet extends BaseServlet {

    private final JobPositionDAO dao = new JobPositionDAO();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(true, "OK", dao.getAll())));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                JobPosition p = dao.getById(id);
                if (p == null) {
                    resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(false, "Không tìm thấy", null)));
                } else {
                    resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(true, "OK", p)));
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        try {
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
            String name = body.has("name") ? body.get("name").getAsString() : null;
            String description = body.has("description") && !body.get("description").isJsonNull()
                    ? body.get("description").getAsString() : null;
            if (name == null || name.isBlank()) {
                resp.getWriter().write(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Tên chức danh không được trống", null)));
                return;
            }
            JobPosition created = dao.add(new JobPosition(name.trim(), description));
            resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(true, "Thêm thành công", created)));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        try {
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
            int id = body.get("id").getAsInt();
            String name = body.get("name").getAsString();
            String description = body.has("description") && !body.get("description").isJsonNull()
                    ? body.get("description").getAsString() : null;
            if (name == null || name.isBlank()) {
                resp.getWriter().write(objectMapper.writeValueAsString(
                        new ApiResponse<>(false, "Tên chức danh không được trống", null)));
                return;
            }
            JobPosition p = new JobPosition(name.trim(), description);
            p.setId(id);
            boolean ok = dao.update(p);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(ok, ok ? "Cập nhật thành công" : "Không tìm thấy", null)));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String idParam = req.getParameter("id");
            if (idParam == null) {
                resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(false, "Thiếu id", null)));
                return;
            }
            boolean ok = dao.delete(Integer.parseInt(idParam));
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(ok, ok ? "Xóa thành công" : "Không tìm thấy", null)));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(false, e.getMessage(), null)));
        }
    }
}
