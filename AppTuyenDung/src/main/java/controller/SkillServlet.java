package controller;

import com.google.gson.Gson;
import dao.SkillDAO;
import dto.ApiResponse;
import exception.BusinessException;
import model.Skills;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/skills")
public class SkillServlet extends BaseServlet {

    private final SkillDAO skillDAO = new SkillDAO();
    private final Gson gson = new Gson();

    private void sendJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(body));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idRaw = req.getParameter("id");
        try {
            if (idRaw != null) {
                Skills skill = skillDAO.getById(Integer.parseInt(idRaw));
                if (skill == null) {
                    sendJson(resp, HttpServletResponse.SC_NOT_FOUND,
                            new ApiResponse<>(false, "Không tìm thấy kỹ năng"));
                } else {
                    sendJson(resp, HttpServletResponse.SC_OK,
                            new ApiResponse<>(true, "Thành công", skill));
                }
            } else {
                List<Skills> list = skillDAO.getAll();
                sendJson(resp, HttpServletResponse.SC_OK,
                        new ApiResponse<>(true, "Danh sách kỹ năng", list));
            }
        } catch (Exception e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiResponse<>(false, "Lỗi máy chủ: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String skillName = req.getParameter("skillName");
        try {
            if (skillName == null || skillName.trim().isEmpty()) {
                throw new BusinessException("Tên kỹ năng không được để trống");
            }
            Skills skill = new Skills(skillName.trim());
            skillDAO.add(skill);
            sendJson(resp, HttpServletResponse.SC_CREATED,
                    new ApiResponse<>(true, "Thêm kỹ năng thành công", skill));
        } catch (BusinessException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiResponse<>(false, "Lỗi máy chủ: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idRaw = req.getParameter("id");
        String skillName = req.getParameter("skillName");
        try {
            if (idRaw == null || skillName == null || skillName.trim().isEmpty()) {
                throw new BusinessException("id và skillName là bắt buộc");
            }
            Skills skill = skillDAO.getById(Integer.parseInt(idRaw));
            if (skill == null) {
                sendJson(resp, HttpServletResponse.SC_NOT_FOUND,
                        new ApiResponse<>(false, "Không tìm thấy kỹ năng"));
                return;
            }
            skill.setSkillName(skillName.trim());
            skillDAO.update(skill);
            sendJson(resp, HttpServletResponse.SC_OK,
                    new ApiResponse<>(true, "Cập nhật kỹ năng thành công", skill));
        } catch (BusinessException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiResponse<>(false, "Lỗi máy chủ: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idRaw = req.getParameter("id");
        try {
            if (idRaw == null) {
                throw new BusinessException("Thiếu id kỹ năng");
            }
            int id = Integer.parseInt(idRaw);
            Skills skill = skillDAO.getById(id);
            if (skill == null) {
                sendJson(resp, HttpServletResponse.SC_NOT_FOUND,
                        new ApiResponse<>(false, "Không tìm thấy kỹ năng"));
                return;
            }
            skillDAO.delete(id);
            sendJson(resp, HttpServletResponse.SC_OK,
                    new ApiResponse<>(true, "Xóa kỹ năng thành công"));
        } catch (BusinessException e) {
            sendJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiResponse<>(false, "Lỗi máy chủ: " + e.getMessage()));
        }
    }
}
