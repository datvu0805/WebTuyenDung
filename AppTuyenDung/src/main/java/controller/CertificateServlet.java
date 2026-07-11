package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.CertificateDTO;
import mapper.CertificateRequestMapper;
import service.CertificateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/certificate/*")
public class CertificateServlet extends HttpServlet {

    private final CertificateService service = new CertificateService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            String path = req.getPathInfo();

            // GET /certificate
            if (path == null || "/".equals(path)) {

                List<CertificateDTO> list = service.getAll();

                resp.setStatus(HttpServletResponse.SC_OK);

                mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Lấy danh sách chứng chỉ thành công!", list));
                return;
            }

            // GET /certificate/{id}
            int id = Integer.parseInt(path.substring(1));

            CertificateDTO dto = service.getById(id);

            if (dto == null) {

                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

                mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, "Không tìm thấy chứng chỉ!", null));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Lấy chứng chỉ thành công!", dto));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, "ID không hợp lệ!", null));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, "Có lỗi hệ thống: " + e.getMessage(), null));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            CertificateDTO dto = CertificateRequestMapper.toDTO(req);

            service.add(dto);

            resp.setStatus(HttpServletResponse.SC_CREATED);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Thêm chứng chỉ thành công!", dto));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, "Có lỗi hệ thống: " + e.getMessage(), null));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            CertificateDTO dto = CertificateRequestMapper.toDTO(req);

            service.update(dto);

            resp.setStatus(HttpServletResponse.SC_OK);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Cập nhật chứng chỉ thành công!", dto));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, "Có lỗi hệ thống: " + e.getMessage(), null));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {

            int id = Integer.parseInt(req.getParameter("id"));

            service.delete(id);

            resp.setStatus(HttpServletResponse.SC_OK);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Xóa chứng chỉ thành công!", id));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, "ID không hợp lệ!", null));

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(false, "Có lỗi hệ thống: " + e.getMessage(), null));
        }
    }
}