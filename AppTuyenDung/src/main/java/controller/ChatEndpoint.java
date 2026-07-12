package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.MessageDAO;
import model.Message;
import service.MessageService;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint: ws://host/AppTuyenDung/ws/chat/{userId}
 * userId là users.id của người đang kết nối.
 */
@ServerEndpoint("/ws/chat/{userId}")
public class ChatEndpoint {

    // Map userId -> Session đang mở
    private static final Map<Integer, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private static final MessageService messageService = new MessageService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") int userId) {
        SESSIONS.put(userId, session);
        System.out.println("[WS] User " + userId + " connected. Total: " + SESSIONS.size());
    }

    @OnClose
    public void onClose(@PathParam("userId") int userId) {
        SESSIONS.remove(userId);
        System.out.println("[WS] User " + userId + " disconnected.");
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.err.println("[WS] Error: " + t.getMessage());
    }

    /**
     * Nhận message từ client dạng JSON:
     * { "type": "chat", "receiverId": 3, "content": "Xin chào" }
     * { "type": "read", "senderId": 5 }   — đánh dấu đã đọc
     */
    @OnMessage
    public void onMessage(String rawJson, @PathParam("userId") int senderId) {
        try {
            JsonObject json = GSON.fromJson(rawJson, JsonObject.class);
            String type = json.has("type") ? json.get("type").getAsString() : "chat";

            if ("chat".equals(type)) {
                int receiverId = json.get("receiverId").getAsInt();
                String content = json.get("content").getAsString().trim();
                if (content.isEmpty()) return;

                // Lưu DB
                Message saved = messageService.send(senderId, receiverId, content);
                if (saved == null) return;

                // Tạo payload gửi cho cả 2 bên
                JsonObject payload = buildMessagePayload(saved, senderId);
                String payloadStr = GSON.toJson(payload);

                // Gửi cho receiver nếu đang online
                Session receiverSession = SESSIONS.get(receiverId);
                if (receiverSession != null && receiverSession.isOpen()) {
                    receiverSession.getBasicRemote().sendText(payloadStr);
                }
                // Gửi lại cho sender để confirm
                Session senderSession = SESSIONS.get(senderId);
                if (senderSession != null && senderSession.isOpen()) {
                    senderSession.getBasicRemote().sendText(payloadStr);
                }

            } else if ("read".equals(type)) {
                int fromId = json.get("senderId").getAsInt();
                new MessageDAO().markRead(fromId, senderId);
            }

        } catch (Exception e) {
            System.err.println("[WS] onMessage error: " + e.getMessage());
        }
    }

    private JsonObject buildMessagePayload(Message msg, int actualSenderId) {
        JsonObject o = new JsonObject();
        o.addProperty("type", "chat");
        o.addProperty("id", msg.getId());
        o.addProperty("senderId", msg.getSenderID().getId());
        o.addProperty("senderName", msg.getSenderID().getFullName());
        o.addProperty("receiverId", msg.getReceiverID().getId());
        o.addProperty("content", msg.getMessageConntent());
        o.addProperty("isRead", msg.isRead());
        LocalDateTime sentAt = msg.getCreatedAt();
        o.addProperty("sentAt", sentAt != null ? sentAt.format(FMT) : LocalDateTime.now().format(FMT));
        return o;
    }
}
