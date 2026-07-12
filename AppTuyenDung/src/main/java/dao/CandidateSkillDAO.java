package dao;

import config.DatabaseConfig;
import mapper.CandidateMapper;
import mapper.SkillMapper;
import model.Candidates;
import model.Skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CandidateSkillDAO extends DatabaseConfig {

    public void add(Candidates candidate, Skill skill) {

        String sql = "INSERT INTO candidate_skills(candidate_id, skill_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());
            ps.setInt(2, skill.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addBatch(Candidates candidate, List<Skill> skills) {

        if (skills == null || skills.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO candidate_skills(candidate_id, skill_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Skill skill : skills) {

                ps.setInt(1, candidate.getId());
                ps.setInt(2, skill.getId());

                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Candidates candidate, Skill skill) {

        String sql = "DELETE FROM candidate_skills WHERE candidate_id = ? AND skill_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());
            ps.setInt(2, skill.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByCandidateId(Candidates candidate) {

        String sql = "DELETE FROM candidate_skills WHERE candidate_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Skill> getSkillsByCandidateId(Candidates candidate) {

        String sql = """
                SELECT s.*
                FROM skills s
                INNER JOIN candidate_skills cs
                    ON s.id = cs.skill_id
                WHERE cs.candidate_id = ?
                """;

        List<Skill> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(SkillMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Candidates> getCandidatesBySkillId(Skill skill) {

        String sql = """
                SELECT c.*
                FROM candidates c
                INNER JOIN candidate_skills cs
                    ON c.id = cs.candidate_id
                WHERE cs.skill_id = ?
                """;

        List<Candidates> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, skill.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(CandidateMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean exists(Candidates candidate, Skill skill) {

        String sql = """
                SELECT 1
                FROM candidate_skills
                WHERE candidate_id = ?
                AND skill_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidate.getId());
            ps.setInt(2, skill.getId());

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}