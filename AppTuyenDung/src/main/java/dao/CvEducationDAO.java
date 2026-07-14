package dao;

import config.DatabaseConfig;
import model.CV;
import model.CandidateEducation;
import model.Candidates;
import model.CvEducation;
import model.EducationLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CvEducationDAO extends DatabaseConfig {

    public void deleteByCvId(int cvId) {

        String sql = "DELETE FROM cv_educations WHERE cv_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cvId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addBatch(int cvId, List<Integer> candidateEducationIds) {

        if (candidateEducationIds == null || candidateEducationIds.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO cv_educations(cv_id, candidate_education_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Integer candidateEducationId : candidateEducationIds) {

                ps.setInt(1, cvId);
                ps.setInt(2, candidateEducationId);

                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Xóa hết rồi thêm lại — dùng khi candidate cập nhật danh sách học vấn gắn vào 1 CV
    public void replaceForCv(int cvId, List<Integer> candidateEducationIds) {
        deleteByCvId(cvId);
        addBatch(cvId, candidateEducationIds);
    }

    // Lấy danh sách học vấn (kèm tên trình độ/trường) đã gắn vào 1 CV
    public List<CvEducation> getByCvId(int cvId) {

        String sql = """
                SELECT cve.id AS cve_id, cve.cv_id, cve.candidate_education_id,
                       ce.candidate_id, ce.education_level_id, ce.school_name, ce.major,
                       ce.start_date, ce.end_date, ce.gpa, ce.description,
                       el.level_name
                FROM cv_educations cve
                JOIN candidate_educations ce ON cve.candidate_education_id = ce.id
                JOIN education_levels el ON ce.education_level_id = el.id
                WHERE cve.cv_id = ?
                """;

        List<CvEducation> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cvId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    CandidateEducation candidateEducation = new CandidateEducation(
                            rs.getInt("candidate_education_id"),
                            new Candidates(rs.getInt("candidate_id")),
                            new EducationLevel(rs.getInt("education_level_id"), rs.getString("level_name")),
                            rs.getString("school_name"),
                            rs.getString("major"),
                            rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null,
                            rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                            rs.getString("gpa"),
                            rs.getString("description")
                    );

                    CV cv = new CV();
                    cv.setId(rs.getInt("cv_id"));

                    CvEducation cvEducation = new CvEducation(rs.getInt("cve_id"), cv, candidateEducation);

                    list.add(cvEducation);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
