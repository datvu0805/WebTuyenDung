package service;

import dao.*;
import dto.CandidateProfileDTO;
import dto.EmployerProfileDTO;
import dto.RegisterCandidateDTO;
import dto.RegisterEmployerDTO;
import model.Company;
import model.Employers;
import model.Role;
import model.Users;
import utils.PasswordUtil;
import validator.UserValidator;

import javax.servlet.http.Part;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final CandidateDAO candicateDAO = new CandidateDAO();
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

    public String updateCandidateProfile(int id, CandidateProfileDTO dto)
            throws SQLException, ClassNotFoundException {

        if (!validator.isValidEmail(dto.getEmail())) {
            return "Email không hợp lệ";
        }

        if (!validator.isValidPhoneNumber(dto.getPhoneNumber())) {
            return "Số điện thoại không hợp lệ";
        }

        Users user = userDAO.getByID(id);

        if (user == null) {
            return "Không tìm thấy người dùng";
        }
        // ✅ check email đúng
        Users existingUser = userDAO.findByEmail(dto.getEmail());
        if (existingUser != null && existingUser.getId() != id) {
            return "Email đã tồn tại";
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setAvatarUrl(dto.getAvatarUrl());

        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isBlank()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth(), formatter));
        }

        try (Connection conn = userDAO.getConnection()) {
            boolean update = userDAO.update(conn, id, user);

            if (!update) {
                return "update thất bại";
            }
        }

        return null;
    }

    public String updateEmployerProfile(int userId, EmployerProfileDTO dto)
            throws SQLException {

        Connection conn = null;

        try {
            conn = userDAO.getConnection();
            conn.setAutoCommit(false); //  bắt đầu transaction

            // ===== update user =====
            Users user = new Users();
            user.setFullName(dto.getFullName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setAddress(dto.getAddress());
            user.setAvatarUrl(dto.getAvatarUrl());

            user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
            Users existingEmail = userDAO.findByEmailExceptId(user.getEmail(), userId);
            if (existingEmail != null) {
                return "update email thất bại";
            }



            boolean updateUser = userDAO.update(conn, userId, user);

            if (!updateUser) {
                conn.rollback();
                return "Update user thất bại";
            }

            // ===== employer =====
            Employers employer = employerDAO.findByUserId(userId);


            conn.commit(); //  thành công hết

            return null;

        } catch (Exception e) {
            if (conn != null) conn.rollback(); //  lỗi → rollback
            e.printStackTrace();
            return "Lỗi hệ thống";
        } finally {
            if (conn != null) conn.close();
        }
    }
    }

