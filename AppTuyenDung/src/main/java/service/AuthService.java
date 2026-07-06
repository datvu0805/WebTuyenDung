package service;

import dao.*;
import model.Company;
import model.Role;
import model.Users;
import utils.PasswordUtil;
import validator.UserValidator;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final CandicateDAO candicateDAO = new CandicateDAO();
    private final EmployerDAO employerDAO = new EmployerDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    private final UserValidator validator = new UserValidator();

    public String registerCandidate(Users user) {
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

        boolean createCandidate = candicateDAO.add(userId);

        if(!createCandidate) {
            return "Tạo candidate thất bại";
        }


        return null;
    }

    public String registerEmployer(Users user,Company company) throws SQLException, ClassNotFoundException {

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
