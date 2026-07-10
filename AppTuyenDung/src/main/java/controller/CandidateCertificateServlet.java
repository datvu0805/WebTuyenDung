package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ApiResponse;
import dto.CandidateCertificateDTO;
import mapper.CandidateCertificateRequestMapper;
import service.CandidateCertificateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/candidate-certificate/*")
public class CandidateCertificateServlet extends HttpServlet {

    private final CandidateCertificateService service = new CandidateCertificateService();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {

            List<CandidateCertificateDTO> list = service.getAll();

            mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Lấy danh sách chứng chỉ thành công!", list));

            return;
        }

        int id = Integer.parseInt(path.substring(1));

        mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Lấy chứng chỉ thành công!", service.getById(id)));

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CandidateCertificateDTO dto = CandidateCertificateRequestMapper.toDTO(req);

        service.add(dto);

        mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Thêm chứng chỉ thành công!", null));

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CandidateCertificateDTO dto = CandidateCertificateRequestMapper.toDTO(req);

        service.update(dto);

        mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Cập nhật chứng chỉ thành công!", null));

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int id = Integer.parseInt(req.getParameter("id"));

        service.delete(id);

        mapper.writeValue(resp.getWriter(), new ApiResponse<>(true, "Xóa chứng chỉ thành công!", null));

    }

}