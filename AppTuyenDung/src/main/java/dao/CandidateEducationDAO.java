package dao;

import config.DatabaseConfig;
import model.CandidateEducation;
import model.Candidates;
import model.EducationLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidateEducationDAO extends DatabaseConfig implements TDAO<CandidateEducation> {

    @Override
    public void add(CandidateEducation entity) {

        String sql = """
                INSERT INTO candidate_educations
                (candidate_id, education_level_id, school_name, major, start_date, end_date, gpa, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getCandidateID().getId());
            ps.setInt(2, entity.getEducationLevelID().getId());
            ps.setString(3, entity.getSchoolName());
            ps.setString(4, entity.getMajor());
            ps.setObject(5, entity.getStartDate());
            ps.setObject(6, entity.getEndDate());
            ps.setString(7, entity.getGpa());
            ps.setString(8, entity.getDescription());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) entity.setId(rs.getInt("id"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(CandidateEducation entity) {

        String sql = """
                UPDATE candidate_educations
                SET education_level_id = ?,
                    school_name = ?,
                    major = ?,
                    start_date = ?,
                    end_date = ?,
                    gpa = ?,
                    description = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getEducationLevelID().getId());
            ps.setString(2, entity.getSchoolName());
            ps.setString(3, entity.getMajor());
            ps.setObject(4, entity.getStartDate());
            ps.setObject(5, entity.getEndDate());
            ps.setString(6, entity.getGpa());
            ps.setString(7, entity.getDescription());
            ps.setInt(8, entity.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM candidate_educations WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CandidateEducation getById(int id) {

        String sql = "SELECT * FROM candidate_educations WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<CandidateEducation> getAll() {

        String sql = "SELECT * FROM candidate_educations ORDER BY id DESC";

        List<CandidateEducation> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<CandidateEducation> getByCandidateId(int candidateId) {

        String sql = "SELECT * FROM candidate_educations WHERE candidate_id = ? ORDER BY id DESC";

        List<CandidateEducation> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidateId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private CandidateEducation mapRow(ResultSet rs) throws SQLException {

        CandidateEducation entity = new CandidateEducation();

        entity.setId(rs.getInt("id"));
        entity.setCandidateID(new Candidates(rs.getInt("candidate_id")));
        entity.setEducationLevelID(new EducationLevel(rs.getInt("education_level_id")));
        entity.setSchoolName(rs.getString("school_name"));
        entity.setMajor(rs.getString("major"));

        java.sql.Date startDate = rs.getDate("start_date");
        if (startDate != null) entity.setStartDate(startDate.toLocalDate());

        java.sql.Date endDate = rs.getDate("end_date");
        if (endDate != null) entity.setEndDate(endDate.toLocalDate());

        entity.setGpa(rs.getString("gpa"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }
}
