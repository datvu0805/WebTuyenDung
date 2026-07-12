package service;

import dao.MessageDAO;
import model.Message;

import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO = new MessageDAO();

    public Message send(int senderId, int receiverId, String content) {
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Nội dung không được trống");
        messageDAO.save(senderId, receiverId, content);
        // Trả về conversation mới nhất (1 tin)
        List<Message> conv = messageDAO.getConversation(senderId, receiverId, 1);
        return conv.isEmpty() ? null : conv.get(conv.size() - 1);
    }

    public List<Message> getConversation(int userId1, int userId2) {
        messageDAO.markRead(userId2, userId1); // đánh dấu đã đọc khi mở hội thoại
        return messageDAO.getConversation(userId1, userId2, 100);
    }

    public List<Message> getRecentConversations(int userId) {
        return messageDAO.getRecentConversations(userId);
    }

    public int countUnread(int userId) {
        return messageDAO.countUnread(userId);
    }
}
