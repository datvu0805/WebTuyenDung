package dao;

import config.DatabaseConfig;
import mapper.JobMapper;
import model.JobSkills;
import model.Jobs;
import model.Skills;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobsDAO extends DatabaseConfig implements TDAO<Jobs> {
    @Override
    public void add(Jobs jobs) {
        String sql = "INSERT INTO jobs (employer_id, title, description, salary, location, experience, quantity, posted_at, expired_at, application_deadline, status, is_hidden_on_expiry, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobs.getEmployerID().getId());
            ps.setString(2, jobs.getTitle());
            ps.setString(3, jobs.getDescription());
            ps.setDouble(4, jobs.getSalary());
            ps.setString(5, jobs.getLocation());
            ps.setString(6, jobs.getExperience());
            ps.setInt(7, jobs.getQuantity());
            ps.setObject(8, jobs.getPostedAt());
            ps.setObject(9, jobs.getExpiredAt());
            ps.setObject(10, jobs.getApplicationDeadline());
            ps.setInt(11, jobs.getStatus());
            ps.setBoolean(12, jobs.getHiddenOnExpiry());
            ps.setObject(13, jobs.getCreatedAt());
            ps.setObject(14, jobs.getUpdatedAt());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // đồng bộ hoá id
                    jobs.setId(rs.getInt("id"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Jobs jobs) {
        String sql = " UPDATE jobs SET employer_id = ?, title = ?, description = ?, salary = ?, location = ?, experience = ?, quantity = ?, posted_at = ?, expired_at = ?, application_deadline = ?, status = ?, is_hidden_on_expiry = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? ";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobs.getEmployerID().getId());
            ps.setString(2, jobs.getTitle());
            ps.setString(3, jobs.getDescription());
            ps.setDouble(4, jobs.getSalary());
            ps.setString(5, jobs.getLocation());
            ps.setString(6, jobs.getExperience());
            ps.setInt(7, jobs.getQuantity());
            ps.setObject(8, jobs.getPostedAt());
            ps.setObject(9, jobs.getExpiredAt());
            ps.setObject(10, jobs.getApplicationDeadline());
            ps.setInt(11, jobs.getStatus());
            ps.setBoolean(12, jobs.getHiddenOnExpiry());
            ps.setInt(13, jobs.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = " DELETE FROM jobs WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Jobs getById(int id) {

        String sql = "SELECT * FROM jobs WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return JobMapper.map(rs);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<Jobs> getAll() {

        String sql = "SELECT * FROM jobs";

        List<Jobs> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(JobMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public void addSkill(Jobs jobsID, Skills skillsID) {
        String sql = "INSERT INTO jobs_skills (jobs_id, skills_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobsID.getId());
            ps.setInt(2, skillsID.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSkill(Jobs jobsID, Skills skillsID) {
        String sql = "DELETE FROM jobs_skills WHERE jobs_id = ? AND skills_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobsID.getId());
            ps.setInt(2, skillsID.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<JobSkills> getSkillsByJob(int jobId) {
        String sql = "SELECT * FROM job_skills WHERE job_id = ?";

        List<JobSkills> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                JobSkills jobSkill = new JobSkills();

                jobSkill.setJobID(new Jobs(rs.getInt("job_id")));
                jobSkill.setSkillID(new Skills(rs.getInt("skill_id")));

                list.add(jobSkill);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }
}
