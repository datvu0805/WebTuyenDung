package dao;

import config.DatabaseConfig;
import mapper.CandidateMapper;
import mapper.JobMapper;
import model.Candidates;
import model.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FavoriteJobDAO extends DatabaseConfig {

    public void add(Candidates candidate, Job job) {

        String sql = "INSERT INTO favorite_jobs(candidate_id, job_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());
            ps.setInt(2, job.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Candidates candidate, Job job) {

        String sql = "DELETE FROM favorite_jobs WHERE candidate_id = ? AND job_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());
            ps.setInt(2, job.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(Candidates candidate, Job job) {

        String sql = """
                SELECT 1
                FROM favorite_jobs
                WHERE candidate_id = ?
                AND job_id = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());
            ps.setInt(2, job.getId());

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Job> getFavoriteJobs(Candidates candidate) {

        String sql = """
                SELECT j.*
                FROM jobs j
                INNER JOIN favorite_jobs f
                    ON j.id = f.job_id
                WHERE f.candidate_id = ?
                """;

        List<Job> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(JobMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Candidates> getCandidatesFavoriteJob(Job job) {

        String sql = """
                SELECT c.*
                FROM candidates c
                INNER JOIN favorite_jobs f
                    ON c.id = f.candidate_id
                WHERE f.job_id = ?
                """;

        List<Candidates> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(CandidateMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public int countFavoriteByJobId(Job job) {

        String sql = """
                SELECT COUNT(*)
                FROM favorite_jobs
                WHERE job_id = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}