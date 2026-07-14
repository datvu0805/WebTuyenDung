package dao;

import config.DatabaseConfig;
import dto.ApplicationDTO;
import exception.BusinessException;
import model.Application;
import model.CV;
import model.Candidates;
import model.Job;
import model.Users;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // Lấy danh sách jobId mà candidate đã ứng tuyển — dùng để loại khỏi danh sách gợi ý
    public Set<Integer> getAppliedJobIds(int candidateID) {
        String sql = "SELECT job_id FROM applications WHERE candidate_id = ?";
        Set<Integer> result = new HashSet<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getInt("job_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách job đã ứng tuyển: " + e.getMessage());
        }
        return result;
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
        // recruiterId = users.id — join qua employers để lấy jobs của nhà tuyển dụng này
        // LEFT JOIN qua cv_certificates/cv_educations để lấy tên chứng chỉ/học vấn candidate gắn vào CV ứng tuyển
        String sql = "SELECT a.id AS app_id, a.cover_letter, a.description, " +
                "c.id AS candidate_id, u.full_name AS candidate_full_name, " +
                "j.title AS job_title, cv.cv_title AS cv_title, cv.file_url, a.status, a.applied_at, " +
                "(SELECT string_agg(cert.certificate_name, ', ') FROM cv_certificates cvc " +
                "   JOIN candidate_certificates cc ON cvc.candidate_certificate_id = cc.id " +
                "   JOIN certificates cert ON cc.certificate_id = cert.id " +
                "   WHERE cvc.cv_id = cv.id) AS attached_certificates, " +
                "(SELECT string_agg(el.level_name, ', ') FROM cv_educations cve " +
                "   JOIN candidate_educations ce ON cve.candidate_education_id = ce.id " +
                "   JOIN education_levels el ON ce.education_level_id = el.id " +
                "   WHERE cve.cv_id = cv.id) AS attached_educations " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.id " +
                "JOIN users u ON c.user_id = u.id " +
                "JOIN jobs j ON a.job_id = j.id " +
                "JOIN employers e ON j.employer_id = e.id " +
                "JOIN cvs cv ON a.cv_id = cv.id " +
                "WHERE e.user_id = ? ORDER BY a.applied_at DESC";

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
                    Users u = new Users();
                    u.setFullName(rs.getString("candidate_full_name"));
                    candidate.setUser(u);
                    dto.setCandidateName(candidate);

                    dto.setJobTitle(rs.getString("job_title"));
                    dto.setCvTitle(rs.getString("cv_title"));
                    dto.setFileUrl(rs.getString("file_url"));
                    dto.setApplieAt(rs.getObject("applied_at", LocalDateTime.class));
                    dto.setStatus(rs.getInt("status"));

                    dto.setAttachedCertificates(splitCommaList(rs.getString("attached_certificates")));
                    dto.setAttachedEducations(splitCommaList(rs.getString("attached_educations")));

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi SQL khi lấy danh sách DTO: " + e.getMessage(), e);
        }
        return list;
    }

    private List<String> splitCommaList(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(value.split(",\\s*")));
    }

    @Override
    public List<Application> getAll() {
        return List.of();
    }

    public ApplicationDTO getApplicationDtoById(int appId) {
        String sql = "SELECT a.id AS app_id, a.cover_letter, a.description, " +
                "c.id AS candidate_id, " +
                "j.title AS job_title, cv.cv_title AS cv_title, cv.file_url, a.status, a.applied_at " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.id " +
                "JOIN jobs j ON a.job_id = j.id " +
                "JOIN cvs cv ON a.cv_id = cv.id " +
                "WHERE a.id = ?"; // Bắn lệnh JOIN để lôi thông tin thật từ các bảng lên theo đúng ID vừa tạo

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ApplicationDTO dto = new ApplicationDTO();

                    Application app = new Application();
                    app.setId(rs.getInt("app_id"));
                    app.setCoverLetter(rs.getString("cover_letter"));
                    app.setDescription(rs.getString("description"));
                    dto.setApplicationID(app);

                    Candidates candidate = new Candidates();
                    candidate.setId(rs.getInt("candidate_id"));
                    dto.setCandidateName(candidate);

                    // Đổ trực tiếp dữ liệu text xịn từ các bảng khác sang DTO
                    dto.setJobTitle(rs.getString("job_title"));
                    dto.setCvTitle(rs.getString("cv_title"));
                    dto.setFileUrl(rs.getString("file_url"));
                    dto.setApplieAt(rs.getObject("applied_at", LocalDateTime.class));
                    dto.setStatus(rs.getInt("status"));

                    return dto;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi SQL khi lấy DTO theo ID: " + e.getMessage(), e);
        }
        return null;
    }

}