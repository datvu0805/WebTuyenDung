package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.FakeBankWebhookDTO;
import service.PaymentService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Receives callbacks from the local Fake-bank container. The simulator does not
 * sign callbacks, so the payment fields are reconciled atomically by
 * {@link PaymentService}; no shared token is required for local Docker use.
 */
@WebServlet("/api/payment/fake-bank/webhook")
public class FakeBankWebhookServlet extends HttpServlet {
    private final PaymentService paymentService = new PaymentService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            FakeBankWebhookDTO webhook = objectMapper.readValue(request.getReader(), FakeBankWebhookDTO.class);
            Map<String, Object> result = paymentService.handleFakeBankWebhook(webhook);

            Map<String, String> body = new HashMap<>();
            body.put("code", String.valueOf(result.getOrDefault("RspCode", "99")));
            body.put("message", String.valueOf(result.getOrDefault("Message", "Unknown")));

            String code = body.get("code");
            int status = "00".equals(code)
                    ? HttpServletResponse.SC_OK
                    : "96".equals(code)
                        ? HttpServletResponse.SC_SERVICE_UNAVAILABLE
                        : HttpServletResponse.SC_BAD_REQUEST;
            write(response, status, body);
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                    "code", "97",
                    "message", "Invalid webhook payload"));
        }
    }

    private void write(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
