package dao;

import model.CV;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CVDAO extends DatabaseConnection{
    // thêm một CV mới(khi ứng viên upload CV lên hệ thômngs
    public boolean insertCV(CV cv) throws SQLException {
        String sql =  "INSERT INTO cvs(candidate_id, cv_title, file_url, avatar_url, description, version, created_at, update_at"
                + "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, cv.getCandidateId());
            ps.setString(2, cv.getCvTitle());
            ps.setString(3, cv.getFileUrl());
            ps.setString(4, cv.getAvatarURl());
            ps.setString(5, cv.getDescription());
            ps.setString(6, cv.getVersion());

            int result = ps.executeUpdate();
            return result > 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // lấy ra CV của từng ứng viên

}
