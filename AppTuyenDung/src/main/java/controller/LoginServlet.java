package controller;

import com.google.gson.Gson;
import dao.CandidateDAO;
import dao.EmployerDAO;
import dto.ApiResponse;
import dto.LoginResponseDTO;
import dto.LoginResult;
import model.Candidates;
import model.Employers;
import model.Users;
import service.AuthService;
import service.FileService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AuthService authService = new AuthService();
    private final FileService fileService = new FileService();
    private final EmployerDAO employerDAO = new EmployerDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> result;

        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            LoginResult loginResult =
                    authService.login(username, password);

            // Đăng nhập thất bại hoặc tài khoản đang bị khóa
            if (!loginResult.isSuccess()) {

                long retryAfterSeconds =
                        loginResult.getRetryAfterSeconds();

                if (retryAfterSeconds > 0) {
                    // 429: đăng nhập sai quá nhiều lần
                    resp.setStatus(429);

                    resp.setHeader(
                            "Retry-After",
                            String.valueOf(retryAfterSeconds)
                    );
                } else {
                    // 401: sai tài khoản hoặc mật khẩu
                    resp.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );
                }

                result = new ApiResponse<>(
                        false,
                        loginResult.getMessage(),
                        null
                );

            } else {
                // Lấy user từ LoginResult
                Users user = loginResult.getUser();

                HttpSession session = req.getSession();

                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute(
                        "role",
                        user.getRole().getRoleName()
                );

                LoginResponseDTO data =
                        new LoginResponseDTO();

                data.setUserId(user.getId());
                data.setUsername(user.getUsername());
                data.setFullName(user.getFullName());
                data.setEmail(user.getEmail());
                data.setPhoneNumber(user.getPhoneNumber());
                data.setAddress(user.getAddress());
                data.setRole(
                        user.getRole().getRoleName()
                );

                /*
                 * Nếu avatarUrl chỉ là object path trong MinIO
                 * thì tạo presigned URL để frontend truy cập được.
                 */
                String avatarUrl = user.getAvatarUrl();

                if (avatarUrl != null
                        && !avatarUrl.isBlank()
                        && !avatarUrl.startsWith("http")) {

                    try {
                        avatarUrl =
                                fileService.getPresignedUrl(avatarUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                data.setAvatarUrl(avatarUrl);

                String roleName =
                        user.getRole().getRoleName();

                // Tài khoản EMPLOYER
                if ("EMPLOYER".equals(roleName)) {

                    Employers employer =
                            employerDAO.findByUserId(user.getId());

                    if (employer != null) {
                        data.setEmployerId(employer.getId());

                        session.setAttribute(
                                "employerId",
                                employer.getId()
                        );
                    }
                }

                // Tài khoản CANDIDATE
                if ("CANDIDATE".equals(roleName)) {

                    Candidates candidate =
                            candidateDAO.findByUserId(user.getId());

                    if (candidate != null) {
                        data.setCandidateId(candidate.getId());

                        session.setAttribute(
                                "candidateId",
                                candidate.getId()
                        );
                    }
                }

                result = new ApiResponse<>(
                        true,
                        loginResult.getMessage(),
                        data
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            result = new ApiResponse<>(
                    false,
                    "Đã xảy ra lỗi khi đăng nhập: "
                            + e.getMessage(),
                    null
            );
        }

        resp.getWriter().write(
                gson.toJson(result)
        );
    }
}