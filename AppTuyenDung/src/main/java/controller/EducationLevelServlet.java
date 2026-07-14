package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.EducationLevelDTO;
import service.EducationLevelService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/education-level/*"})
public class EducationLevelServlet extends BaseServlet {

    private final EducationLevelService educationLevelService = new EducationLevelService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /education-level
            if (pathInfo == null || pathInfo.equals("/")) {

                List<EducationLevelDTO> list = educationLevelService.getAllEducationLevels();

                ApiResponse<List<EducationLevelDTO>> response =
                        new ApiResponse<>(true, "Lấy danh sách trình độ học vấn thành công!", list);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));

            } else {

                // GET /education-level/1
                int id = Integer.parseInt(pathInfo.substring(1));

                EducationLevelDTO dto = educationLevelService.getEducationLevelById(id);

                if (dto == null) {

                    ApiResponse<EducationLevelDTO> response =
                            new ApiResponse<>(false, "Không tìm thấy trình độ học vấn", null);

                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print(objectMapper.writeValueAsString(response));
                    return;
                }

                ApiResponse<EducationLevelDTO> response =
                        new ApiResponse<>(true, "Lấy trình độ học vấn thành công!", dto);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));
            }

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "ID không hợp lệ", null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            EducationLevelDTO dto = objectMapper.readValue(req.getReader(), EducationLevelDTO.class);

            educationLevelService.addEducationLevel(dto);

            ApiResponse<EducationLevelDTO> response =
                    new ApiResponse<>(true, "Thêm trình độ học vấn thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "Có lỗi hệ thống xảy ra: " + e.getMessage(), null)));

        } finally {

            resp.getWriter().flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            EducationLevelDTO dto = objectMapper.readValue(req.getReader(), EducationLevelDTO.class);

            if (dto.getId() == null) {
                throw new IllegalArgumentException("ID không hợp lệ");
            }

            educationLevelService.updateEducationLevel(dto.getId(), dto);

            ApiResponse<EducationLevelDTO> response =
                    new ApiResponse<>(true, "Cập nhật trình độ học vấn thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            int id = Integer.parseInt(req.getParameter("id"));

            educationLevelService.deleteEducationLevel(id);

            ApiResponse<Integer> response =
                    new ApiResponse<>(true, "Xóa trình độ học vấn thành công!", id);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "ID không hợp lệ", null)));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, e.getMessage(), null)));
        }
    }
}
