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

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String path = req.getServletPath();

        if (path.equals("/login")
                || path.equals("/register-candidate")
                || path.equals("/register-employer")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")) {

            chain.doFilter(request, response);
            return;
        }

        if (session == null || session.getAttribute("userId") == null) {

            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");

            resp.getWriter().write("""
                    {
                        "success": false,
                        "message": "Bạn chưa đăng nhập"
                    }
                    """);

            return;
        }

        chain.doFilter(request, response);
    }
}