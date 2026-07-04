package dao;

import config.DatabaseConfig;
import model.Role;
import model.Transactions;
import model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DatabaseConfig implements DDAO<Users> {

    @Override
    public Users getByID(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try(
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                       return new Users(
                               rs.getString("username"),
                               rs.getString("password"),
                               rs.getString("full_name"),
                               rs.getString("avatar_url"),
                               rs.getString("email"),
                               rs.getDate("date_of_birth").toLocalDate(),
                               rs.getString("phone_number"),
                               rs.getString("address"),
                               new Role(rs.getString("role_name"))
                       );
                    }
                }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Users findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try(
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return new Users(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("avatar_url"),
                            rs.getString("email"),
                            rs.getDate("date_of_birth").toLocalDate(),
                            rs.getString("phone_number"),
                            rs.getString("address"),
                            new Role(rs.getString("role_name"))
                    );
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(Users user) {
        String sql="INSERT INTO Users(username,password,full_name,avatar_url,email,date_of_birth,phone_number,address,role_id) VALUES (?,?,?,?,?,?,?,?,?)";
        try(
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
                ){
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getPassword());
            ps.setString(3,user.getFullName());
            ps.setString(4,user.getAvatarUrl());
            ps.setString(5,user.getEmail());
            ps.setDate(6, Date.valueOf(user.getDateOfBirth()));
            ps.setString(7, user.getPhoneNumber());
            ps.setString(8, user.getAddress());
            ps.setInt(9,user.getRole().getId());

            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace() ;
        }
    }

    @Override
    public void update(Users user) {
        String sql = "UPDATE users set username = ?,password = ?,full_name = ?,avatar_url = ?, email = ?,date_of_birth = ?,phone_number = ?,address = ?,role_id = ?, updated_at = CURRENT_TIMESTAMP  where id = ?";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getAvatarUrl());
            ps.setString(5, user.getEmail());
            ps.setDate(6, Date.valueOf(user.getDateOfBirth()));
            ps.setString(7, user.getPhoneNumber());
            ps.setString(8, user.getAddress());
            ps.setInt(9,user.getRole().getId());
            ps.setInt(10,user.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try(
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
                ){
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Users> getAll() {
        List<Users> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        ;
        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
                ){
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Users user = new Users(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("avatar_url"),
                            rs.getString("email"),
                            rs.getDate("date_of_birth").toLocalDate(),
                            rs.getString("phone_number"),
                            rs.getString("address"),
                            new Role(rs.getString("role_name"))
                    );
                    list.add(user);
                }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
}
