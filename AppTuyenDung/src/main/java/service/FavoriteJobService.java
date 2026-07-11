package service;

import dao.CandidateDAO;
import dao.FavoriteJobDAO;
import dao.JobDAO;
import dto.FavoriteJobDTO;
import model.Candidates;
import model.Job;
import validator.FavoriteJobValidator;

import java.util.List;

public class FavoriteJobService {

    private final FavoriteJobDAO favoriteJobDAO = new FavoriteJobDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final JobDAO jobDAO = new JobDAO();

    /**
     * Thêm Job vào danh sách yêu thích
     */
    public void add(FavoriteJobDTO dto) {

        FavoriteJobValidator.validate(dto);

        Candidates candidate = candidateDAO.getByID(dto.getCandidateId());
        if (candidate == null) {
            throw new IllegalArgumentException("Ứng viên không tồn tại.");
        }

        Job job = jobDAO.getById(dto.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        if (favoriteJobDAO.exists(candidate, job)) {
            throw new IllegalArgumentException("Công việc đã có trong danh sách yêu thích.");
        }

        favoriteJobDAO.add(candidate, job);
    }

    /**
     * Xóa Job khỏi danh sách yêu thích
     */
    public void delete(FavoriteJobDTO dto) {

        FavoriteJobValidator.validate(dto);

        Candidates candidate = candidateDAO.getByID(dto.getCandidateId());
        if (candidate == null) {
            throw new IllegalArgumentException("Ứng viên không tồn tại.");
        }

        Job job = jobDAO.getById(dto.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        favoriteJobDAO.delete(candidate, job);
    }


    /**
     * Kiểm tra đã yêu thích hay chưa
     */
    public boolean exists(int candidateId, int jobId) {

        Candidates candidate = candidateDAO.getByID(candidateId);
        Job job = jobDAO.getById(jobId);

        if (candidate == null || job == null) {
            return false;
        }

        return favoriteJobDAO.exists(candidate, job);
    }

    /**
     * Lấy danh sách Job yêu thích của ứng viên
     */
    public List<Job> getFavoriteJobs(int candidateId) {

        Candidates candidate = candidateDAO.getByID(candidateId);

        if (candidate == null) {
            throw new IllegalArgumentException("Ứng viên không tồn tại.");
        }

        return favoriteJobDAO.getFavoriteJobs(candidate);
    }

    /**
     * Lấy danh sách ứng viên đã yêu thích Job
     */
    public List<Candidates> getCandidatesFavoriteJob(int jobId) {

        Job job = jobDAO.getById(jobId);

        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        return favoriteJobDAO.getCandidatesFavoriteJob(job);
    }

    /**
     * Đếm số lượt yêu thích của Job
     */
    public int countFavoriteByJobId(int jobId) {

        Job job = jobDAO.getById(jobId);

        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        return favoriteJobDAO.countFavoriteByJobId(job);
    }
}