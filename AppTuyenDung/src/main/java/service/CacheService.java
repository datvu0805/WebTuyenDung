package service;

public class CacheService {

    private final RedisService redisService;

    public CacheService() {
        this.redisService = new RedisService();
    }

    public void clearAdminStatistic() {
        redisService.delete("admin:statistic");
    }

    public void clearCacheCandidate(int candidateId) {
        redisService.delete("Candidate: "+candidateId);
    }

    public void clearCacheEmployer(int employerId) {
        redisService.delete("Admin: "+employerId);
    }

    public void clearCacheCompany(int companyId) {
        redisService.delete("Company: "+companyId);
    }
}