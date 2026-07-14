package service;

import dao.ApplicationDAO;
import dao.CandidateDAO;
import dao.CandidateSkillDAO;
import dao.FavoriteJobDAO;
import dao.JobDAO;
import dao.JobSkillDAO;
import dto.JobDTO;
import mapper.JobMapper;
import model.Candidates;
import model.Job;
import model.JobSkill;
import model.Skill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gợi ý việc làm cho candidate. Có 2 chế độ:
 *
 *  - AI (bật qua system_settings.ai_recommendation_enabled): gọi sang AI service (Python) để tính điểm
 *    dựa trên skill + nội dung job + mức lương mong muốn (TF-IDF/cosine similarity + Jaccard).
 *  - Rule-based (mặc định, và fallback khi AI service lỗi/timeout): tính điểm bằng Jaccard skill overlap
 *    + salary overlap score, cộng tie-break theo lượt yêu thích và ngày đăng.
 */
public class RecommendationService {

    private static final double RULE_SKILL_WEIGHT = 0.7;
    private static final double RULE_SALARY_WEIGHT = 0.3;

    private final JobDAO jobDAO = new JobDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final CandidateSkillDAO candidateSkillDAO = new CandidateSkillDAO();
    private final JobSkillDAO jobSkillDAO = new JobSkillDAO();
    private final FavoriteJobDAO favoriteJobDAO = new FavoriteJobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final SystemSettingService systemSettingService = new SystemSettingService();
    private final AiRecommendationClient aiRecommendationClient = new AiRecommendationClient();

    private static class ScoredJob {
        Job job;
        double score;
        int favoriteCount;
    }

    public List<JobDTO> recommend(int candidateId, int limit) {

        if (candidateId <= 0) {
            throw new IllegalArgumentException("ID ứng viên không hợp lệ");
        }

        if (limit <= 0) {
            limit = 10;
        }

        Candidates candidate = candidateDAO.findById(candidateId);
        if (candidate == null) {
            candidate = new Candidates(candidateId);
        }

        List<Skill> candidateSkills = candidateSkillDAO.getSkillsByCandidateId(candidate);
        Set<Integer> candidateSkillIds = candidateSkills.stream().map(Skill::getId).collect(Collectors.toSet());

        Set<Integer> appliedJobIds = applicationDAO.getAppliedJobIds(candidateId);

        List<Job> activeJobs = jobDAO.getAllActive().stream()
                .filter(job -> !appliedJobIds.contains(job.getId()))
                .collect(Collectors.toList());

        if (systemSettingService.isAiRecommendationEnabled()) {
            try {
                return recommendWithAi(candidate, candidateSkills, activeJobs, limit);
            } catch (Exception e) {
                // AI service lỗi/timeout — fallback êm sang rule-based, không làm hỏng trải nghiệm candidate
                System.err.println("AI recommendation thất bại, fallback rule-based: " + e.getMessage());
            }
        }

        return recommendRuleBased(candidate, candidateSkillIds, activeJobs, limit);
    }

    private List<JobDTO> recommendWithAi(
            Candidates candidate, List<Skill> candidateSkills, List<Job> activeJobs, int limit
    ) throws IOException {

        List<String> skillNames = candidateSkills.stream().map(Skill::getSkillName).collect(Collectors.toList());

        Map<Integer, Job> jobById = new HashMap<>();
        List<Map<String, Object>> jobPayload = new ArrayList<>();

        for (Job job : activeJobs) {

            jobById.put(job.getId(), job);

            List<String> jobSkillNames = getJobSkillNames(job.getId());

            Map<String, Object> payload = new HashMap<>();
            payload.put("id", job.getId());
            payload.put("title", job.getTitle());
            payload.put("description", job.getDescription());
            payload.put("skills", jobSkillNames);
            payload.put("minSalary", job.getMinSalary());
            payload.put("maxSalary", job.getMaxSalary());

            jobPayload.add(payload);
        }

        List<AiRecommendationClient.JobScoreResult> scores = aiRecommendationClient.recommend(
                skillNames, candidate.getDesiredMinSalary(), candidate.getDesiredMaxSalary(), jobPayload
        );

        List<JobDTO> result = new ArrayList<>();

        for (int i = 0; i < scores.size() && i < limit; i++) {
            Job job = jobById.get(scores.get(i).jobId);
            if (job != null) {
                result.add(JobMapper.toDTO(job));
            }
        }

        return result;
    }

    private List<JobDTO> recommendRuleBased(
            Candidates candidate, Set<Integer> candidateSkillIds, List<Job> activeJobs, int limit
    ) {

        List<ScoredJob> scoredJobs = new ArrayList<>();

        for (Job job : activeJobs) {

            ScoredJob scoredJob = new ScoredJob();
            scoredJob.job = job;
            scoredJob.favoriteCount = favoriteJobDAO.countFavoriteByJobId(job);

            double skillScore = candidateSkillIds.isEmpty()
                    ? 0.0
                    : jaccardSimilarity(candidateSkillIds, getJobSkillIds(job.getId()));

            double salaryScore = salaryOverlapScore(
                    candidate.getDesiredMinSalary(), candidate.getDesiredMaxSalary(),
                    job.getMinSalary(), job.getMaxSalary()
            );

            scoredJob.score = RULE_SKILL_WEIGHT * skillScore + RULE_SALARY_WEIGHT * salaryScore;

            scoredJobs.add(scoredJob);
        }

        // Sắp theo: điểm tổng hợp giảm dần -> lượt yêu thích giảm dần -> ngày đăng mới nhất
        scoredJobs.sort(
                Comparator.comparingDouble((ScoredJob sj) -> sj.score).reversed()
                        .thenComparing(Comparator.comparingInt((ScoredJob sj) -> sj.favoriteCount).reversed())
                        .thenComparing((ScoredJob sj) -> sj.job.getPostedAt(),
                                Comparator.nullsLast(Comparator.reverseOrder()))
        );

        List<JobDTO> result = new ArrayList<>();

        for (int i = 0; i < scoredJobs.size() && i < limit; i++) {
            result.add(JobMapper.toDTO(scoredJobs.get(i).job));
        }

        return result;
    }

    private List<String> getJobSkillNames(int jobId) {
        List<String> names = new ArrayList<>();
        for (JobSkill js : jobSkillDAO.getByJobId(jobId)) {
            Skill skill = js.getSkillID();
            if (skill != null && skill.getSkillName() != null) {
                names.add(skill.getSkillName());
            }
        }
        return names;
    }

    private Set<Integer> getJobSkillIds(int jobId) {
        Set<Integer> ids = new HashSet<>();
        for (JobSkill js : jobSkillDAO.getByJobId(jobId)) {
            ids.add(js.getSkillID().getId());
        }
        return ids;
    }

    private double jaccardSimilarity(Set<Integer> a, Set<Integer> b) {

        if (a.isEmpty() && b.isEmpty()) {
            return 0.0;
        }

        Set<Integer> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<Integer> union = new HashSet<>(a);
        union.addAll(b);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }

    /** Tỉ lệ chồng lấp giữa khoảng lương mong muốn của candidate và khoảng lương của job, 0 nếu thiếu dữ liệu hoặc không overlap. */
    private double salaryOverlapScore(Double desiredMin, Double desiredMax, Double jobMin, Double jobMax) {

        if (desiredMin == null && desiredMax == null) {
            return 0.0;
        }

        if (jobMin == null && jobMax == null) {
            return 0.0;
        }

        double dMin = desiredMin != null ? desiredMin : 0;
        double dMax = desiredMax != null ? desiredMax : dMin;
        double jMin = jobMin != null ? jobMin : 0;
        double jMax = jobMax != null ? jobMax : jMin;

        double overlapStart = Math.max(dMin, jMin);
        double overlapEnd = Math.min(dMax, jMax);

        if (overlapEnd <= overlapStart) {
            return 0.0;
        }

        double overlap = overlapEnd - overlapStart;
        double desiredRange = Math.max(dMax - dMin, 1.0);

        return Math.min(overlap / desiredRange, 1.0);
    }
}
