package controller;

import com.google.gson.Gson;
import dto.ApiResponse;
import dto.EmployerProfileDTO;
import service.AuthService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/employer/profile/update")
public class UpdateProfileEmployer extends  BaseServlet{
    private final Gson gson = new Gson();
    private final AuthService employerService = new AuthService();

    @Override
    protected void doPut(HttpServletRequest request , HttpServletResponse response) throws IOException{
        response.setContentType("application/json;charset=utf-8");
        request.setCharacterEncoding("utf-8");

        HttpSession session = request.getSession(false);
        int employerId = (Integer)session.getAttribute("employerId");

        ApiResponse<?> result ;

        try {
            EmployerProfileDTO dto = gson.fromJson(request.getReader(), EmployerProfileDTO.class);
            String error = employerService.updateEmployerProfile(employerId, dto);

            if (error == null) {
                result = new ApiResponse<>(true, "Cập nhật candidate thành công", null);
            } else {
                result = new ApiResponse<>(false, error, null);
            }
        }catch (Exception e){
            e.printStackTrace();

            result = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null)
        ;}

        response.getWriter().write(gson.toJson(result));
    }
}
