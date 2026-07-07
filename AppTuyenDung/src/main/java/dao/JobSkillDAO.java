package dao;

import config.DatabaseConfig;
import model.JobSkills;
import model.Jobs;
import model.Skills;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JobSkillDAO extends DatabaseConfig {

    public void add(JobSkills jobSkill) {

        String sql = "INSERT INTO jobs_skills (jobs_id, skills_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobSkill.getJobID().getId());
            ps.setInt(2, jobSkill.getSkillID().getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(JobSkills jobSkill) {

        String sql = "DELETE FROM jobs_skills WHERE jobs_id = ? AND skills_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobSkill.getJobID().getId());
            ps.setInt(2, jobSkill.getSkillID().getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<JobSkills> getByJobId(int jobId) {

        String sql = "SELECT * FROM jobs_skills WHERE jobs_id = ?";

        List<JobSkills> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    JobSkills jobSkill = new JobSkills();

                    jobSkill.setJobID(new Jobs(rs.getInt("jobs_id")));
                    jobSkill.setSkillID(new Skills(rs.getInt("skills_id")));

                    list.add(jobSkill);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<JobSkills> getBySkillId(int skillId) {

        String sql = "SELECT * FROM jobs_skills WHERE skills_id = ?";

        List<JobSkills> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, skillId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    JobSkills jobSkill = new JobSkills();

                    jobSkill.setJobID(new Jobs(rs.getInt("jobs_id")));
                    jobSkill.setSkillID(new Skills(rs.getInt("skills_id")));

                    list.add(jobSkill);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean exists(int jobId, int skillId) {

        String sql = " SELECT 1 FROM jobs_skills WHERE jobs_id = ? AND skills_id = ? ";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ps.setInt(2, skillId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}