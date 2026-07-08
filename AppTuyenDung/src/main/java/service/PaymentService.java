package service;

import config.DatabaseConfig;
import dao.ServicePackagesDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import dao.UserServicesMDDAO;
import model.ServicePackages;
import model.Transactions;
import model.UserServicesMD;
import model.Users;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class PaymentService {
    private final UserDAO userDAO = new UserDAO();
    private final ServicePackagesDAO packagesDAO = new ServicePackagesDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UserServicesMDDAO userServicesMDDAO = new UserServicesMDDAO();

    public void purchasepackage(int userId, int packageID) throws SQLException {
        Connection conn = null;
        try{
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // để đồng bộ hóa dữ liệu

            // 1. kiểm tra xem Gói DV đã toond tại hay chưa
            ServicePackages pkg = packagesDAO.getPackageById(conn, packageID);
            if (pkg == null){
                throw new IllegalArgumentException("Gói dịch vụ không tồn tại! ");
            }

            // 2. kiểm tra thông tin  người dùng mua gói DV
            Users users = userDAO.getUserByIDForUpdate(conn, userId);
            if(users == null){
                throw new IllegalArgumentException("Không tìm thấy tài khoản người dùng");
            }

            // Đảm bảo người dùng mua gói dịch vụ được dùng gói Premium
            String userRole = users.getRole().getRoleName();
            String targetAudience = pkg.getTargetAudience();

            if(!userRole.equalsIgnoreCase(targetAudience)){
                throw new IllegalArgumentException("Tài khoản của bạn là vai trò " + userRole +
                         " , không thể đăng kí gói dịch vụ Premium  dành cho " + pkg.getPackageName());
            }

            // hệ thống ghi nhận đã thanh toán
            Transactions trans = new Transactions();
            trans.setUserID(userId);
            trans.setTransactionType("Mua gói premium");
            trans.setAmount(Double.valueOf(pkg.getPrice()));
            trans.setStatus(1);// 1 thành công
            trans.setContent("Tài khoản " + userRole + " kích hoạt thành công gói premium!");
            transactionDAO.insertTransaction(conn, trans);

            // cấp chu kỳ dịch vụ cao cấp Premium
            UserServicesMD us = new UserServicesMD();
            LocalDate now = LocalDate.now();
            us.setStartDate(LocalDate.now());
            us.setEndDate(now.plusDays(pkg.getDurationDays()));
            us.setStatus("1"); // đang kích hoạt sử dụng

            userServicesMDDAO.insertUserService(conn, us);
            conn.commit(); // ko lỗi
        } catch (Exception e) {
            if (conn != null){
                try {
                    conn.rollback();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
            throw e;
        }finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close(); // Giải phóng kết nối vật lý trả lại về Pool điều phối cho yêu cầu tiếp theo
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
