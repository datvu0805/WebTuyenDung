package service;

import dao.UserDAO;
import dto.AdminProfileDTO;
import model.Users;
import validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public class AdminService {

    private final UserDAO userDAO = new UserDAO();
    AdminProfileDTO dto = new AdminProfileDTO();
    private final UserValidator validator = new UserValidator();
    public AdminProfileDTO getAdminProfile(int adminId) {
        Users users = userDAO.getByID(adminId);
        if (users == null) {
            return null;
        }
        dto.setUserId(users.getId());
        dto.setAvatarUrl(users.getAvatarUrl());
        dto.setUsername(users.getUsername());
        dto.setFullName(users.getFullName());
        dto.setEmail(users.getEmail());
        dto.setAddress(users.getAddress());
        dto.setPhoneNumber(users.getPhoneNumber());
        if (users.getDateOfBirth() != null) {
            dto.setDateOfBirth(users.getDateOfBirth().toString());
        }
        dto.setRole(users.getRole().getRoleName());

        return dto;
    }


    public String updateAdminProfile(int userId,AdminProfileDTO dto){
        Users user = userDAO.getByID(userId);

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUsername(dto.getUsername());
        user.setAvatarUrl(dto.getAvatarUrl());

        if(!validator.isValidPhoneNumber( dto.getPhoneNumber())){
            return "Số điện thoại không hợp lệ";
        }

        if (!validator.isValidEmail(dto.getEmail())){
            return "Email không hợp lệ";
        }

        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isBlank()) {
            user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        }

        boolean update = userDAO.update(userId,user);
        if (!update) {
            return "Update thất bại";
        }

        return null;
    }
}
