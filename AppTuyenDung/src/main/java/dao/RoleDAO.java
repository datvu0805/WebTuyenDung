package dao;

import config.DatabaseConfig;
import model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleDAO extends DatabaseConfig{
    public Role findById(int id) {
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement("Select * from roles where id=?");
        ){
            ps.setInt(1,id);

            try(ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    return new Role(
                            rs.getString("id"),
                            rs.getString("role_name")
                    );
                }
            }
        }catch (SQLException e){
           e.printStackTrace() ;
        }
        return null;
    }

    public Role findByName(String Name) {
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * from roles where role_name=?")
        ){
            ps.setString(1,Name);

            try(ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    return new Role(
                            rs.getString("id"),
                            rs.getString("role_name")
                    );
                }
            }
    }catch (SQLException e){
        e.printStackTrace() ;
        }
        return null;
    }
}
