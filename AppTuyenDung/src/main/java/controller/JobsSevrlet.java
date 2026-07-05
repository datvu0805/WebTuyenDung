package controller;

import dao.JobsDAO;
import model.Employers;
import model.Jobs;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(urlPatterns = {"/jobs"})
public class JobsSevrlet extends BaseServlet {
    private JobsDAO jobsDAO = new JobsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Jobs API is running");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        int employerId = Integer.parseInt(req.getParameter("employerId"));

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        double salary = Double.parseDouble(req.getParameter("salary"));

        String location = req.getParameter("location");
        String experience = req.getParameter("experience");

        int quantity = Integer.parseInt(req.getParameter("quantity"));

        LocalDateTime postedAt = LocalDateTime.parse(req.getParameter("postedAt"));

        LocalDateTime expiredAt = LocalDateTime.parse(req.getParameter("expiredAt"));

        LocalDateTime applicationDeadline = LocalDateTime.parse(req.getParameter("applicationDeadline"));

        Short status = Short.parseShort(req.getParameter("status"));

        boolean hiddenOnExpiry = Boolean.parseBoolean(req.getParameter("hiddenOnExpiry"));
        Jobs jobs = new Jobs(new Employers(employerId), title, description, salary, location, experience, quantity, postedAt, expiredAt, applicationDeadline, status, hiddenOnExpiry);

//        resp.sendRedirect(req.getContextPath() + "/jobs");
        jobsDAO.add(jobs);

        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Add job success! ID = " + jobs.getId());
    }
}
