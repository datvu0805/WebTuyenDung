package dao;

import config.DatabaseConfig;
import dto.ApplicationDTO;
import exception.BusinessException;
import model.Application;
import model.CV;
import model.Candidates;
import model.Job;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO extends DatabaseConfig implements IDAO<Application> {

    @Override
    public void add(Application app) {
        String sql = "INSERT INTO applications(candidate_id, job_id, cv_id, applied_at, cover_letter, description, status, created_at, updated_at)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, app.getCandidateID().getId());
            ps.setInt(2, app.getJodID().getId());
            ps.setInt(3, app.getCvID().getId());
            ps.setObject(4, app.getAppliedAt());
            ps.setString(5, app.getCoverLetter());
            ps.setString(6, app.getDescription());
            ps.setInt(7, app.getStatus());
            ps.setObject(8, app.getCreatedAt());
            ps.setObject(9, app.getUpdatedAt());

            ps.executeUpdate();
            // lấy id tự tăng của postgres trả lại cho app
            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    app.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void update(Application app) {
        String sql = "UPDATE applications SET status = ?, updated_at = ? WHERE id = ?";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, app.getStatus());
            ps.setObject(2, app.getUpdatedAt());
            ps.setInt(3, app.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật trạng thái: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM applications WHERE id = ?";
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("lỗi SQL khi xóa đơn ứng tuyển: " + e.getMessage());
        }
    }

    public Application getByID(int id){
        String sql = "SELECT * FROM applications WHERE id = ?";
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Application app = new Application();
                    app.setId(rs.getInt("id"));
                    app.setStatus(rs.getInt("status"));
                    app.setAppliedAt((LocalDateTime) rs.getObject("applied_at"));

                    Candidates c = new Candidates();
                    c.setId(rs.getInt("candidate_id"));
                    app.setCandidateID(c);

                    Job j = new Job();
                    j.setId(rs.getInt("job_id"));
                    app.setJodID(j);

                    CV cv = new CV();
                    cv.setId(rs.getInt("cv_id"));
                    app.setCvID(cv);
                        return app;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi sql khi tìm đơn ứng tuyển: " +e.getMessage());
        }
        return null;
    }

    // đếm số đơn ứng tuyển
    public int countApplications(int candidateID, int jobID) {
        String sql = "SELECT COUNT(*) FROM applications WHERE candidate_id = ? AND job_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, candidateID);
            ps.setInt(2, jobID);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }catch (SQLException e){
            throw new BusinessException("Lỗi kiểm tra số lượng ứng viên: " + e.getMessage());
        }
        return 0;
    }

    // lấy ra ds đơn ứng tuyển của từng ứng viên
    public List<ApplicationDTO> getApplicationsForRecruiter(int recruiterId) {
        List<ApplicationDTO> list = new ArrayList<>();
        String sql = "SELECT a.id AS app_id, a.cover_letter, a.description, " +
                "c.id AS candidate_id, " +
                "j.title AS job_title, cv.title AS cv_title, cv.file_url, a.status, a.applied_at " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.id " +
                "JOIN jobs j ON a.job_id = j.id " +
                "JOIN cvs cv ON a.cv_id = cv.id " +
                "WHERE j.recruiter_id = ? ORDER BY a.applied_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApplicationDTO dto = new ApplicationDTO();

                    Application app = new Application();
                    app.setId(rs.getInt("app_id"));
                    app.setCoverLetter(rs.getString("cover_letter"));
                    app.setDescription(rs.getString("description"));
                    dto.setApplicationID(app);

                    Candidates candidate = new Candidates();
                    candidate.setId(rs.getInt("candidate_id"));
                    dto.setCandidateName(candidate);

                    dto.setJobTitle(rs.getString("job_title"));
                    dto.setCvTitle(rs.getString("cv_title"));
                    dto.setFileUrl(rs.getString("file_url"));
                    dto.setApplieAt(rs.getObject("applied_at", LocalDateTime.class));
                    dto.setStatus(rs.getInt("status"));

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi SQL khi lấy danh sách DTO: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Application> getAll() {
        return List.of();
    }


}