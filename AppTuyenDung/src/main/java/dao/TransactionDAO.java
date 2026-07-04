package dao;

import config.DatabaseConfig;
import model.Transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TransactionDAO extends DatabaseConfig implements IDAO<Transactions> {

    // ghi lại lịch sử dòng tiền
    @Override
    public void add(Transactions transactions) {
        String sql = "INSERT INTO transactions(user_id, transaction_type, amount, status, content, created_at, update_at)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
//           ps.setInt(1, transactions.getUserID());
           ps.setString(2, transactions.getTransactionType());
           ps.setDouble(3, transactions.getAmount());
           ps.setInt(4, transactions.getStatus());
           ps.setString(5, transactions.getContent());
           ps.setObject(6, transactions.getCreatedAt());
           ps.setObject(7, transactions.getUpdatedAt());

           ps.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Transactions entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Transactions> getAll() {
        return List.of();
    }
}
