package dao;

import config.DatabaseConfig;
import model.Transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO extends DatabaseConfig implements IDAO<Transactions> {

    /**
     * Insert pending transaction, return generated id.
     * status: 0=pending, 1=success, 2=failed
     */
    public long insertTransaction(Connection conn, Transactions transactions) {
        String sql = """
            INSERT INTO transactions(user_id, transaction_type, amount, status, payment_status, content, package_id, txn_ref,
                                     payment_provider, created_at, updated_at)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING id
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactions.getUserID());
            ps.setString(2, transactions.getTransactionType());
            ps.setDouble(3, transactions.getAmount());
            ps.setInt(4, transactions.getStatus());
            ps.setString(5, transactions.getPaymentStatus());
            ps.setString(6, transactions.getContent());
            if (transactions.getPackageId() != null) {
                ps.setInt(7, transactions.getPackageId());
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }
            ps.setString(8, transactions.getTxnRef());
            ps.setString(9, transactions.getPaymentProvider());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    transactions.setId((int) id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tạo giao dịch: " + e.getMessage(), e);
        }
        throw new RuntimeException("Không lấy được id giao dịch");
    }

    public Transactions findByTxnRef(Connection conn, String txnRef) {
        String sql = """
            SELECT id, user_id, transaction_type, amount, status, payment_status, content, package_id, txn_ref,
                   payment_provider, provider_transaction_id
            FROM transactions
            WHERE txn_ref = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txnRef);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm giao dịch: " + e.getMessage(), e);
        }
        return null;
    }

    public Transactions findByTxnRefAndUserId(String txnRef, int userId) {
        String sql = """
            SELECT id, user_id, transaction_type, amount, status, payment_status, content, package_id, txn_ref,
                   payment_provider, provider_transaction_id
            FROM transactions
            WHERE txn_ref = ? AND user_id = ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txnRef);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm giao dịch của người dùng: " + e.getMessage(), e);
        }
    }

    public boolean markSuccess(Connection conn, long transactionId, String providerTransactionId) {
        String sql = """
            UPDATE transactions
            SET status = 1,
                payment_status = 'SUCCESS',
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
              AND status = 0
              AND provider_transaction_id = ?
              AND provider_transaction_id IS NOT NULL
              AND provider_transaction_id <> ''
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, transactionId);
            ps.setString(2, providerTransactionId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật giao dịch thành công: " + e.getMessage(), e);
        }
    }

    public boolean setProviderTransactionId(Connection conn, long transactionId, String providerTransactionId) {
        String sql = """
            UPDATE transactions
            SET provider_transaction_id = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND status = 0
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, providerTransactionId);
            ps.setLong(2, transactionId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu mã giao dịch provider: " + e.getMessage(), e);
        }
    }

    public boolean markFailed(Connection conn, long transactionId, String content, String paymentStatus) {
        String sql = """
            UPDATE transactions
            SET status = 2,
                payment_status = ?,
                content = COALESCE(?, content),
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND status = 0
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus);
            ps.setString(2, content);
            ps.setLong(3, transactionId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật giao dịch thất bại: " + e.getMessage(), e);
        }
    }

    public List<Transactions> findByUserId(int userId) {
        String sql = """
            SELECT id, user_id, transaction_type, amount, status, payment_status, content, package_id, txn_ref,
                   payment_provider, provider_transaction_id
            FROM transactions
            WHERE user_id = ?
            ORDER BY created_at DESC
            """;
        List<Transactions> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy lịch sử giao dịch: " + e.getMessage(), e);
        }
        return list;
    }

    private Transactions map(ResultSet rs) throws SQLException {
        Transactions t = new Transactions();
        t.setId(rs.getInt("id"));
        t.setUserID(rs.getInt("user_id"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setAmount(rs.getDouble("amount"));
        t.setStatus(rs.getInt("status"));
        t.setPaymentStatus(rs.getString("payment_status"));
        t.setContent(rs.getString("content"));
        int pkgId = rs.getInt("package_id");
        if (!rs.wasNull()) {
            t.setPackageId(pkgId);
        }
        t.setTxnRef(rs.getString("txn_ref"));
        t.setPaymentProvider(rs.getString("payment_provider"));
        t.setProviderTransactionId(rs.getString("provider_transaction_id"));
        return t;
    }

    @Override
    public void add(Transactions entity) {
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
