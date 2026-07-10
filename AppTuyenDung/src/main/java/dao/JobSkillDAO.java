package dao;

import config.DatabaseConfig;
import model.JobSkill;
import model.Job;
import model.Skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JobSkillDAO extends DatabaseConfig {

    public void add(JobSkill jobSkill) {

        String sql = "INSERT INTO job_skills (job_id, skill_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobSkill.getJobID().getId());
            ps.setInt(2, jobSkill.getSkillID().getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(JobSkill jobSkill) {

        String sql = "DELETE FROM job_skills WHERE job_id = ? AND skill_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobSkill.getJobID().getId());
            ps.setInt(2, jobSkill.getSkillID().getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
// tìm kiếm trong job này đang có yêu cầu kỹ năng nào
    public List<JobSkill> getByJobId(int jobId) {

        String sql = "SELECT * FROM job_skills WHERE job_id = ?";

        List<JobSkill> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    JobSkill jobSkill = new JobSkill();

                    jobSkill.setJobID(new Job(rs.getInt("job_id")));
                    jobSkill.setSkillID(new Skill(rs.getInt("skill_id")));

                    list.add(jobSkill);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
// ngược lại tìm xem skill thì có nhưng job nào đang cần  tới nó
    public List<JobSkill> getBySkillId(int skillId) {

        String sql = "SELECT * FROM job_skills WHERE skill_id = ?";

        List<JobSkill> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, skillId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    JobSkill jobSkill = new JobSkill();

                    jobSkill.setJobID(new Job(rs.getInt("job_id")));
                    jobSkill.setSkillID(new Skill(rs.getInt("skill_id")));

                    list.add(jobSkill);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
//kiểm tra xem trong data thì job đó đã có skill đó chưa khác với validator
    public boolean exists(int jobId, int skillId) {

        String sql = " SELECT 1 FROM job_skills WHERE job_id = ? AND skill_id = ? ";

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