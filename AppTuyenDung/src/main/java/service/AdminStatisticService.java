package service;

import dao.JobDAO;
import dao.SkillDAO;
import dao.UserDAO;
import dto.AdminStatisticDTO;

public class AdminStatisticService {
    UserDAO userDAO = new UserDAO();
    AdminStatisticDTO dto = new AdminStatisticDTO();
    SkillDAO skillDAO = new SkillDAO();
    JobDAO jobDAO = new JobDAO();
    public AdminStatisticDTO getStatistic() {
        dto.setTotalUsers(userDAO.totalUsers());
        dto.setTotalUserByRole(userDAO.countUserByRole());
        dto.setTotalJobs(jobDAO.totalJob());
        dto.setTotalSkills(skillDAO.totalSkill());
        return dto;
    }
}
