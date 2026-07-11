package dao;

import config.DatabaseConfig;
import model.CV;
import model.Candidates;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CVDAO   implements IDAO<CV>{
    // thêm một CV mới(khi ứng viên upload CV lên hệ thômngs
    @Override
    public void add(CV cv) {
        String sql =  "INSERT INTO cvs(candidate_id, cv_title, file_url, description, version, created_at, updated_at)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            ps.setInt(1,cv.getCandidateId());
            ps.setString(2, cv.getCvTitle());
            ps.setString(3, cv.getFileUrl());
//            ps.setString(4, cv.getAvatarURl());
            ps.setString(4, cv.getDescription());
            ps.setString(5, cv.getVersion());
            ps.setObject(6, cv.getCreatedAt());
            ps.setObject(7, cv.getUpdatedAt());

            ps.executeUpdate();

            // id tự tăng khi đc sinh ra trong db
            try(ResultSet generateKeys = ps.getGeneratedKeys()) {
                if(generateKeys.next()){
                    int generateID = generateKeys.getInt(1);
                    cv.setId(generateID);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(CV entity) {

    }

    @Override
    public void delete(int id) {

    }

    // lấy ra tất cả CV của từng ứng viên
    public List<CV> getCVByCandidateID(int candidateID) {
        List<CV> list = new ArrayList<>();

        String sql = "SELECT id, candidate_id, cv_title, file_url, description, version, created_at, updated_at " +
                "FROM cvs WHERE candidate_id = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CV cv = new CV();
                    cv.setId(rs.getInt("id"));

                    Candidates candidates = new Candidates();
                    candidates.setId(rs.getInt("candidate_id"));
                    cv.setCandidateId(candidates);

                    cv.setCvTitle(rs.getString("cv_title"));
                    cv.setFileUrl(rs.getString("file_url"));
                    cv.setDescription(rs.getString("description"));
                    cv.setVersion(rs.getString("version"));

                    cv.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
                    cv.setUpdatedAt(rs.getObject("updated_at", java.time.LocalDateTime.class));

                    list.add(cv);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách CV theo Candidate ID: " + e.getMessage(), e);
        }
        return list;
    }

    // lấy 1 CV theo id
    public CV getById(int id){
        String sql = "SELECT id, file_url FROM cvs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    CV cv = new CV();
                    cv.setId(rs.getInt("id"));
                    cv.setFileUrl(rs.getString("file_url"));
                    return cv;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    // xóa một cv theo id

    public void deleteID(int cvID, int candidateID){
        String sql = "DELETE FROM cvs WHERE id = ? AND candidate_id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, cvID);
            ps.setInt(2, candidateID);

            ps.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<CV> getAll() {
        return List.of();
    }


}
