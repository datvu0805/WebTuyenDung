package filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        // không cần khởi tạo gì
    }

    @Override
    public void destroy() {
        // không cần dọn dẹp gì
    }

    // Các path không cần đăng nhập
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
            "/login", "/register-candidate", "/register-employer"
    ));

    // Phân quyền theo role: path prefix -> danh sách role được phép
    // Nếu path không có trong map này thì mọi role đã đăng nhập đều truy cập được
    private static final Map<String, Set<String>> ROLE_REQUIRED = new HashMap<>();

    static {
        // Chỉ EMPLOYER mới được tạo/sửa/xóa job
        ROLE_REQUIRED.put("POST:/jobs", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("PUT:/jobs", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("DELETE:/jobs", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("POST:/job-skills", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("DELETE:/job-skills", setOf("EMPLOYER"));

        // Chỉ EMPLOYER cập nhật trạng thái đơn (action=updateStatus)
        // Chỉ CANDIDATE mới nộp đơn (action=submit)
        // — kiểm tra trong servlet vì cùng path POST /api/aplication

        // Chỉ CANDIDATE mới upload CV
        ROLE_REQUIRED.put("POST:/UploadCV", setOf("CANDIDATE"));


        // Upload avatar — cả CANDIDATE lẫn EMPLOYER đều được
        // (không cần entry trong ROLE_REQUIRED — mọi user đã đăng nhập đều qua được)

        //chỉ admin mới đc làm
        ROLE_REQUIRED.put("POST:/admin/company/create", setOf("ADMIN"));
        ROLE_REQUIRED.put("PUT:/admin/company/update", setOf("ADMIN"));
        ROLE_REQUIRED.put("DELETE:/admin/company/delete", setOf("ADMIN"));
        ROLE_REQUIRED.put("GET:/admin/company/list", setOf("ADMIN"));


    }

    private static Set<String> setOf(String... roles) {
        return new HashSet<>(Arrays.asList(roles));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // --- CORS headers để React FE (localhost:5173) có thể gọi API ---
        String origin = req.getHeader("Origin");
        if (origin != null) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        }

        // Preflight OPTIONS request — trả về ngay
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = req.getServletPath();
        String method = req.getMethod();

        // Public paths — cho qua
        if (PUBLIC_PATHS.contains(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")) {
            chain.doFilter(request, response);
            return;
        }

        // Kiểm tra đã đăng nhập chưa
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "Bạn chưa đăng nhập");
            return;
        }

        // Kiểm tra RBAC , admin có quyền dùng tất cả api của role khác
        String roleKey = method + ":" + path;
        Set<String> allowedRoles = ROLE_REQUIRED.get(roleKey);
        if (allowedRoles != null) {
            String userRole = (String) session.getAttribute("role");
            if (userRole == null ||  (!allowedRoles.contains(userRole) && !"ADMIN".equals(userRole))) {
                sendJson(resp, HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void sendJson(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }
}
