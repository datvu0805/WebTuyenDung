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
            "/login", "/register-candidate", "/register-employer",
            // Fake-bank callback is public because the local simulator has no user session.
            "/api/payment/fake-bank/webhook"
    ));

    // Phân quyền theo role: path prefix -> danh sách role được phép
    // Nếu path không có trong map này thì mọi role đã đăng nhập đều truy cập được
    private static final Map<String, Set<String>> ROLE_REQUIRED = new HashMap<>();

    static {
        // Chỉ EMPLOYER mới được tạo/sửa/xóa job
        ROLE_REQUIRED.put("POST:/jobs", setOf("EMPLOYER","ADMIN"));
        ROLE_REQUIRED.put("PUT:/jobs", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("DELETE:/jobs", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("POST:/job-skills", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("DELETE:/job-skills", setOf("EMPLOYER"));

        // Chỉ EMPLOYER cập nhật trạng thái đơn (action=updateStatus)
        // Chỉ CANDIDATE mới nộp đơn (action=submit)
        // — kiểm tra trong servlet vì cùng path POST /api/aplication

        // Chỉ CANDIDATE mới upload CV
        ROLE_REQUIRED.put("POST:/UploadCV", setOf("CANDIDATE"));

        // Chỉ CANDIDATE mới được quản lý kỹ năng của chính mình và yêu thích job
        ROLE_REQUIRED.put("POST:/candidate-skills", setOf("CANDIDATE"));
        ROLE_REQUIRED.put("PUT:/candidate-skills", setOf("CANDIDATE"));
        ROLE_REQUIRED.put("DELETE:/candidate-skills", setOf("CANDIDATE"));
        ROLE_REQUIRED.put("POST:/favorite-jobs", setOf("CANDIDATE"));
        ROLE_REQUIRED.put("DELETE:/favorite-jobs", setOf("CANDIDATE"));

        // Danh mục kỹ năng (catalog) — chỉ ADMIN mới được tạo/sửa/xóa.
        // EMPLOYER và CANDIDATE chỉ được xem (GET) để chọn kỹ năng cho job/hồ sơ.
        ROLE_REQUIRED.put("POST:/skill", setOf("ADMIN"));
        ROLE_REQUIRED.put("PUT:/skill", setOf("ADMIN"));
        ROLE_REQUIRED.put("DELETE:/skill", setOf("ADMIN"));

        // Danh mục chứng chỉ (catalog) — chỉ ADMIN mới được tạo/sửa/xóa, mọi role đã login xem được.
        ROLE_REQUIRED.put("POST:/certificate", setOf("ADMIN"));
        ROLE_REQUIRED.put("PUT:/certificate", setOf("ADMIN"));
        ROLE_REQUIRED.put("DELETE:/certificate", setOf("ADMIN"));

        // Danh mục trình độ học vấn (catalog) — chỉ ADMIN mới được tạo/sửa/xóa.
        ROLE_REQUIRED.put("POST:/education-level", setOf("ADMIN"));
        ROLE_REQUIRED.put("PUT:/education-level", setOf("ADMIN"));
        ROLE_REQUIRED.put("DELETE:/education-level", setOf("ADMIN"));

        // Chỉ CANDIDATE mới được quản lý học vấn của chính mình
        ROLE_REQUIRED.put("POST:/candidate-education", setOf("CANDIDATE"));
        ROLE_REQUIRED.put("PUT:/candidate-education", setOf("CANDIDATE"));
        ROLE_REQUIRED.put("DELETE:/candidate-education", setOf("CANDIDATE"));

        // Chỉ EMPLOYER mới được gắn/xóa yêu cầu học vấn cho job (ownership check trong servlet)
        ROLE_REQUIRED.put("POST:/job-educations", setOf("EMPLOYER"));
        ROLE_REQUIRED.put("DELETE:/job-educations", setOf("EMPLOYER"));

        // Chỉ CANDIDATE mới được gắn chứng chỉ/học vấn vào CV của chính mình (ownership check trong servlet)
        ROLE_REQUIRED.put("PUT:/cv-certificates", setOf("CANDIDATE"));
        ROLE_REQUIRED.put("PUT:/cv-educations", setOf("CANDIDATE"));

        // Cấu hình hệ thống (bật/tắt AI gợi ý...) — chỉ ADMIN được xem và sửa.
        ROLE_REQUIRED.put("GET:/admin/settings", setOf("ADMIN"));
        ROLE_REQUIRED.put("PUT:/admin/settings", setOf("ADMIN"));


        // Upload avatar — cả CANDIDATE lẫn EMPLOYER đều được
        // (không cần entry trong ROLE_REQUIRED — mọi user đã đăng nhập đều qua được)

        //chỉ admin mới đc làm
        ROLE_REQUIRED.put("POST:/admin/company/create", setOf("ADMIN"));
        ROLE_REQUIRED.put("PUT:/admin/company/update", setOf("ADMIN"));
        ROLE_REQUIRED.put("DELETE:/admin/company/delete", setOf("ADMIN"));
        ROLE_REQUIRED.put("GET:/admin/company/list", setOf("ADMIN", "EMPLOYER", "CANDIDATE"));

        // Job positions - chỉ ADMIN mới được thêm/sửa/xóa, mọi user đã đăng nhập đều xem được
        ROLE_REQUIRED.put("POST:/admin/job-positions", setOf("ADMIN"));
        ROLE_REQUIRED.put("PUT:/admin/job-positions", setOf("ADMIN"));
        ROLE_REQUIRED.put("DELETE:/admin/job-positions", setOf("ADMIN"));
        ROLE_REQUIRED.put("GET:/admin/job-positions", setOf("ADMIN", "EMPLOYER", "CANDIDATE"));
        ROLE_REQUIRED.put("POST:/admin/job-positions/import", setOf("ADMIN"));
        ROLE_REQUIRED.put("GET:/admin/statistics", setOf("ADMIN"));
        ROLE_REQUIRED.put("GET:/admin/statistic", setOf("ADMIN"));
        ROLE_REQUIRED.put("GET:/admin/statistics/export", setOf("ADMIN"));
        ROLE_REQUIRED.put("GET:/admin/statistic/export", setOf("ADMIN"));

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
                || path.startsWith("/images/")
                || path.startsWith("/ws/")) {
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
