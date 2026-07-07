package service;

import dao.*;
import dto.RegisterCandidateDTO;
import dto.RegisterEmployerDTO;
import model.Company;
import model.Role;
import model.Users;
import utils.PasswordUtil;
import validator.UserValidator;

import javax.servlet.http.Part;
import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final CandicateDAO candicateDAO = new CandicateDAO();
    private final EmployerDAO employerDAO = new EmployerDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();


    private final UserValidator validator = new UserValidator();

    private final FileService fileService = new FileService();


    public String registerCandidate(RegisterCandidateDTO dto, Part avatar) {

        Users user = new Users();

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());

        String error = validator.validRegister(user.getUsername(),user.getPassword(),user.getEmail(), user.getPhoneNumber());

        if(error != null) {
            return error;
        }

        if(userDAO.getByUsername(user.getUsername()) != null) {
            return "Tên đằng nhập đã tồn tại";
        }
        if(userDAO.findByEmail(user.getEmail()) != null) {
            return "Email đã tồn tại";
        }
        Role role = roleDAO.findByName("CANDIDATE");
        user.setRole(role);

        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        int userId = userDAO.addAndReturnId(user);
        if(userId == -1) {
            return "Tạo tài khoản thất bại";
        }

        String avatarUrl = fileService.uploadImage(avatar, "avatar", userId);

        if (avatarUrl != null) {
            boolean updatedAvatar = userDAO.updateAvatar(userId, avatarUrl);

            if (!updatedAvatar) {
                return "Cập nhật avatar thất bại";
            }
        }

        boolean createCandidate = candicateDAO.add(userId);

        if (!createCandidate) {
            return "Tạo candidate thất bại";
        }


        return null;
    }

    public String registerEmployer(RegisterEmployerDTO dto) throws SQLException, ClassNotFoundException {

        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());

        Company company = new Company();
        company.setCompanyName(dto.getCompanyName());
        company.setDescription(dto.getDescription());

        String error = validator.validRegister(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhoneNumber()
        );

        if (error != null) {
            return error;
        }

        if (userDAO.getByUsername(user.getUsername()) != null) {
            return "Tên đăng nhập đã tồn tại";
        }

        if (userDAO.findByEmail(user.getEmail()) != null) {
            return "Email đã tồn tại";
        }

        Role role = roleDAO.findByName("EMPLOYER");
        user.setRole(role);

        user.setPassword(
                PasswordUtil.hashPassword(user.getPassword())
        );

        int userId = userDAO.addAndReturnId(user);

        if (userId == -1) {
            return "Tạo tài khoản employer thất bại";
        }

        int companyId = companyDAO.add(company);

        if (companyId == -1) {
            return "Tạo công ty thất bại";
        }

        int employerId = employerDAO.add(userId, companyId);

        if (employerId == -1) {
            return "Tạo employer thất bại";
        }

        return null;
    }
    public Users login(String username, String password) {

        Users user = userDAO.getByUsername(username);

        if (user == null) {
            return null;
        }

        boolean validPassword =
                PasswordUtil.verifyPassword(password, user.getPassword());

        if (!validPassword) {
            return null;
        }

        return user;
    }




}
