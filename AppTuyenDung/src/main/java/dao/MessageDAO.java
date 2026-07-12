package dao;

import config.DatabaseConfig;
import model.Message;
import model.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends DatabaseConfig {

    public long save(int senderId, int receiverId, String content) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_content, sent_at, is_read, created_at, updated_at) " +
                     "VALUES (?,?,?,NOW(),false,NOW(),NOW()) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, content);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu tin nhắn: " + e.getMessage(), e);
        }
        return -1;
    }

    public List<Message> getConversation(int userId1, int userId2, int limit) {
        String sql = "SELECT m.id, m.sender_id, m.receiver_id, m.message_content, m.sent_at, m.is_read, " +
                     "su.full_name AS sender_name, ru.full_name AS receiver_name " +
                     "FROM messages m " +
                     "JOIN users su ON m.sender_id = su.id " +
                     "JOIN users ru ON m.receiver_id = ru.id " +
                     "WHERE (m.sender_id=? AND m.receiver_id=?) OR (m.sender_id=? AND m.receiver_id=?) " +
                     "ORDER BY m.sent_at DESC LIMIT ?";
        List<Message> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId1); ps.setInt(2, userId2);
            ps.setInt(3, userId2); ps.setInt(4, userId1);
            ps.setInt(5, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(0, mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy hội thoại: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Message> getRecentConversations(int userId) {
        String sql = "SELECT DISTINCT ON (LEAST(m.sender_id,m.receiver_id), GREATEST(m.sender_id,m.receiver_id)) " +
                     "m.id, m.sender_id, m.receiver_id, m.message_content, m.sent_at, m.is_read, " +
                     "su.full_name AS sender_name, ru.full_name AS receiver_name " +
                     "FROM messages m " +
                     "JOIN users su ON m.sender_id = su.id " +
                     "JOIN users ru ON m.receiver_id = ru.id " +
                     "WHERE m.sender_id=? OR m.receiver_id=? " +
                     "ORDER BY LEAST(m.sender_id,m.receiver_id), GREATEST(m.sender_id,m.receiver_id), m.sent_at DESC";
        List<Message> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách hội thoại: " + e.getMessage(), e);
        }
        return list;
    }

    public void markRead(int senderId, int receiverId) {
        String sql = "UPDATE messages SET is_read=true, updated_at=NOW() WHERE sender_id=? AND receiver_id=? AND is_read=false";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId); ps.setInt(2, receiverId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đánh dấu đã đọc: " + e.getMessage(), e);
        }
    }

    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id=? AND is_read=false";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đếm unread: " + e.getMessage(), e);
        }
        return 0;
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setId((int) rs.getLong("id"));
        msg.setMessageConntent(rs.getString("message_content"));
        msg.setRead(rs.getBoolean("is_read"));

        Users sender = new Users();
        sender.setId(rs.getInt("sender_id"));
        sender.setFullName(rs.getString("sender_name"));
        msg.setSenderID(sender);

        Users receiver = new Users();
        receiver.setId(rs.getInt("receiver_id"));
        receiver.setFullName(rs.getString("receiver_name"));
        msg.setReceiverID(receiver);

        Timestamp ts = rs.getTimestamp("sent_at");
        if (ts != null) msg.setCreatedAt(ts.toLocalDateTime());

        return msg;
    }
}
