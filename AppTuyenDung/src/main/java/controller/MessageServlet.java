package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApiResponse;
import model.Message;
import service.MessageService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/api/messages")
public class MessageServlet extends HttpServlet {

    private final MessageService messageService = new MessageService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private void json(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(mapper.writeValueAsString(body));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        int myId = (int) session.getAttribute("userId");

        try {
            if ("history".equals(action)) {
                // GET /api/messages?action=history&withUserId=X
                int withUserId = Integer.parseInt(req.getParameter("withUserId"));
                List<Message> msgs = messageService.getConversation(myId, withUserId);
                json(resp, 200, new ApiResponse<>(true, "OK", toJsonList(msgs)));

            } else if ("recent".equals(action)) {
                // GET /api/messages?action=recent
                List<Message> convs = messageService.getRecentConversations(myId);
                json(resp, 200, new ApiResponse<>(true, "OK", toJsonList(convs)));

            } else if ("unread".equals(action)) {
                int count = messageService.countUnread(myId);
                Map<String, Object> data = new HashMap<>();
                data.put("count", count);
                json(resp, 200, new ApiResponse<>(true, "OK", data));

            } else {
                json(resp, 400, new ApiResponse<>(false, "Unknown action", null));
            }
        } catch (Exception e) {
            json(resp, 500, new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    private List<Map<String, Object>> toJsonList(List<Message> msgs) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Message m : msgs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", m.getId());
            map.put("senderId", m.getSenderID().getId());
            map.put("senderName", m.getSenderID().getFullName());
            map.put("receiverId", m.getReceiverID().getId());
            map.put("receiverName", m.getReceiverID().getFullName());
            map.put("content", m.getMessageConntent());
            map.put("isRead", m.isRead());
            LocalDateTime sentAt = m.getCreatedAt();
            map.put("sentAt", sentAt != null ? sentAt.format(FMT) : null);
            result.add(map);
        }
        return result;
    }
}
