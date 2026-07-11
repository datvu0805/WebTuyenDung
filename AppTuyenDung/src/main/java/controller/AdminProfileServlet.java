package controller;

import com.google.gson.Gson;
import dto.AdminProfileDTO;
import dto.ApiResponse;
import service.AdminService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/profile")
public class AdminProfileServlet extends BaseServlet {

    private final AdminService adminService = new AdminService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req , HttpServletResponse res) throws IOException {

        res.setContentType("application/json;charset=UTF-8");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        int Id  = (Integer) session.getAttribute("userId");

        AdminProfileDTO dto = adminService.getAdminProfile(Id);

        if (dto == null) {
            res.getWriter().write(gson.toJson(
                    new ApiResponse<>(false,"Khồng thấy admin",null)
            ));
            return;
        }
            res.getWriter().write(gson.toJson(
                    new ApiResponse<>(true,"Lấy thông tin thành công",dto)
            ));

    }

    @Override
    protected void doPut(HttpServletRequest req , HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        AdminProfileDTO dto = gson.fromJson(req.getReader(), AdminProfileDTO.class);

        String error = adminService.updateAdminProfile(userId, dto);

        if(error == null){
            res.getWriter().write(gson.toJson(new ApiResponse<>(true,"Cập nhật thành công",null)));
        }else {
            res.getWriter().write(gson.toJson(new ApiResponse<>(false,error,null)));
        }
    }
}
