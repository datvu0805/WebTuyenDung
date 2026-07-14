package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Gọi sang AI service (Python/FastAPI) để lấy điểm gợi ý việc làm theo skill + nội dung + mức lương.
 * Timeout ngắn để không làm chậm trải nghiệm candidate khi AI service chậm/không phản hồi —
 * lỗi ở đây sẽ được RecommendationService bắt và fallback sang tính điểm rule-based.
 */
public class AiRecommendationClient {

    private static final String AI_SERVICE_URL =
            System.getenv().getOrDefault("AI_SERVICE_URL", "http://ai-service:8000");

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class JobScoreResult {
        public int jobId;
        public double score;
    }

    /**
     * @throws IOException nếu gọi thất bại (network, timeout, response lỗi) — caller phải catch và fallback
     */
    public List<JobScoreResult> recommend(
            List<String> candidateSkills,
            Double desiredMinSalary,
            Double desiredMaxSalary,
            List<Map<String, Object>> jobs
    ) throws IOException {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("candidateSkills", candidateSkills);
        requestBody.put("desiredMinSalary", desiredMinSalary);
        requestBody.put("desiredMaxSalary", desiredMaxSalary);
        requestBody.put("jobs", jobs);

        String json = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(AI_SERVICE_URL + "/recommend")
                .post(RequestBody.create(json, JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("AI service trả về lỗi: " + response.code());
            }

            String responseBody = response.body().string();

            return objectMapper.readValue(
                    responseBody,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, JobScoreResult.class)
            );
        }
    }
}
