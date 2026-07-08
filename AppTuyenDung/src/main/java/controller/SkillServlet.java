package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.SkillDTO;
import mapper.SkillRequestMapper;
import service.SkillService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/skill/*"})
public class SkillServlet extends BaseServlet {

    private final SkillService skillService = new SkillService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String pathInfo = req.getPathInfo();

            // GET /skills
            if (pathInfo == null || pathInfo.equals("/")) {

                List<SkillDTO> skills = skillService.getAllSkills();

                ApiResponse<List<SkillDTO>> response = new ApiResponse<>(true, "Lấy danh sách kỹ năng thành công!", skills);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));

            } else {

                // GET /skills/1
                int id = Integer.parseInt(pathInfo.substring(1));

                SkillDTO dto = skillService.getSkillById(id);

                if (dto == null) {

                    ApiResponse<SkillDTO> response = new ApiResponse<>(false, "Không tìm thấy kỹ năng", null);

                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print(objectMapper.writeValueAsString(response));
                    return;
                }

                ApiResponse<SkillDTO> response = new ApiResponse<>(true, "Lấy kỹ năng thành công!", dto);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(objectMapper.writeValueAsString(response));
            }

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, "ID không hợp lệ", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            SkillDTO dto = SkillRequestMapper.toDTO(req);

            skillService.addSkill(dto);

            ApiResponse<SkillDTO> response = new ApiResponse<>(true, "Thêm kỹ năng thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, "Có lỗi hệ thống xảy ra: " + e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

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

            int id = Integer.parseInt(req.getParameter("id"));

            SkillDTO dto = SkillRequestMapper.toDTO(req);

            skillService.updateSkill(id, dto);

            ApiResponse<SkillDTO> response = new ApiResponse<>(true, "Cập nhật kỹ năng thành công!", dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, "ID không hợp lệ", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            int id = Integer.parseInt(req.getParameter("id"));

            skillService.deleteSkill(id);

            ApiResponse<Integer> response = new ApiResponse<>(true, "Xóa kỹ năng thành công!", id);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, "ID không hợp lệ", null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            ApiResponse<Object> response = new ApiResponse<>(false, e.getMessage(), null);

            resp.getWriter().print(objectMapper.writeValueAsString(response));
        }
    }
}