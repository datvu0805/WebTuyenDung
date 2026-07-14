package dao;

import config.DatabaseConfig;
import mapper.EducationLevelMapper;
import mapper.JobMapper;
import model.EducationLevel;
import model.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JobEducationDAO extends DatabaseConfig {

    public void add(Job job, EducationLevel educationLevel) {

        String sql = "INSERT INTO job_educations(job_id, education_level_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());
            ps.setInt(2, educationLevel.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Job job, EducationLevel educationLevel) {

        String sql = "DELETE FROM job_educations WHERE job_id = ? AND education_level_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());
            ps.setInt(2, educationLevel.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<EducationLevel> getEducationLevelsByJob(Job job) {

        String sql = """
                SELECT el.*
                FROM education_levels el
                INNER JOIN job_educations je
                    ON el.id = je.education_level_id
                WHERE je.job_id = ?
                """;

        List<EducationLevel> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(EducationLevelMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Job> getJobsByEducationLevel(EducationLevel educationLevel) {

        String sql = """
                SELECT j.*
                FROM jobs j
                INNER JOIN job_educations je
                    ON j.id = je.job_id
                WHERE je.education_level_id = ?
                """;

        List<Job> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, educationLevel.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(JobMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean exists(int jobId, int educationLevelId) {

        String sql = "SELECT 1 FROM job_educations WHERE job_id = ? AND education_level_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, educationLevelId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
