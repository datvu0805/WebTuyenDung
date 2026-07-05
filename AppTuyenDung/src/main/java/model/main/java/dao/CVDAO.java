package model.main.java.dao;

import model.main.java.config.DatabaseConfig;
import model.main.java.model.CV;
import model.main.java.model.Candidates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CVDAO extends DatabaseConfig implements IDAO<CV> {
    // thêm một CV mới(khi ứng viên upload CV lên hệ thômngs
    @Override
    public void add(CV cv) {
        String sql =  "INSERT INTO cvs(candidate_id, cv_title, file_url, avatar_url, description, version, created_at, update_at)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1,cv.getCandidateId());
            ps.setString(2, cv.getCvTitle());
            ps.setString(3, cv.getFileUrl());
            ps.setString(4, cv.getAvatarURl());
            ps.setString(5, cv.getDescription());
            ps.setString(6, cv.getVersion());
            ps.setObject(7, cv.getCreatedAt());
            ps.setObject(8, cv.getUpdatedAt());

            int result = ps.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(CV entity) {

    }

    // lấy ra tất cả CV của từng ứng viên

    public List<CV> getCVByCandidateID(int candidateID){
        List<CV> list = new ArrayList<>();
        String sql = "SELECT * FROM cvs WHERE candidate_id = ?";

        try(Connection conn = getConnection();
            PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, candidateID);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    CV cv = new CV();
                    cv.setId(rs.getInt("id"));

                    Candidates candidates = new Candidates();
                    candidates.setId( rs.getInt("candidate_id"));
                    cv.setCandidateId(candidates);

                    cv.setCvTitle(rs.getString("cv_title"));
                    cv.setFileUrl(rs.getString("file_url"));
                    cv.setAvatarURl(rs.getString("avatar_url"));
                    cv.setDescription(rs.getString("description"));
                    cv.setVersion(rs.getString("version"));
                    cv.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                    cv.setUpdatedAt(rs.getObject("update_at", LocalDateTime.class));

                    list.add(cv);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    // xóa một cv theo id
    @Override
    public void delete(int cvID){
        String sql = "DELETE FROM cvs WHERE id = ?";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, cvID);

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
