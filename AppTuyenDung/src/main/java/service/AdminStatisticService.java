package service;

import dao.JobDAO;
import dao.SkillDAO;
import dao.UserDAO;
import dto.AdminStatisticDTO;

public class AdminStatisticService {


    private final UserDAO userDAO = new UserDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final JobDAO jobDAO = new JobDAO();

    private final RedisService redisService = new RedisService();
    private static final String CACHE_KEY = "admin:statistic";
    private final  long CACHE_TTL_SECONDS = 60;


    public AdminStatisticDTO getStatistic() {
        //Lấy dữ liệu từ trong redis trc
        AdminStatisticDTO cacheData = redisService.getObjiect(CACHE_KEY, AdminStatisticDTO.class);

        if(cacheData != null){
            return cacheData;
        }

        //không có cache thì truy vấn posgres
        AdminStatisticDTO dto = new AdminStatisticDTO();

        dto.setTotalUsers(userDAO.totalUsers());
        dto.setTotalUserByRole(userDAO.countUserByRole());
        dto.setTotalJobs(jobDAO.totalJob());
        dto.setTotalSkills(skillDAO.totalSkill());

        //Lưu kết quả vào redis
        redisService.setObjiect(CACHE_KEY,dto,CACHE_TTL_SECONDS);

        return dto;
    }

    public boolean isCached() {
        return redisService.exists("admin:statistic");
    }
}
