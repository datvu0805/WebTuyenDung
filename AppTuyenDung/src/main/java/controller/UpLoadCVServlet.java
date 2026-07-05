package controller;

import config.MinIOConfig;
import dao.CVDAO;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import model.CV;
import model.Candidates;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/UploadCV")
@MultipartConfig(
        fileSizeThreshold = 0,  // 2MB bộ đệm
        maxFileSize = 1024 * 1024 * 10,       // Nâng lên 10MB/file để thoải mái dung lượng
        maxRequestSize = 1024 * 1024 * 30     // 30MB tổng request (vì gửi cả CV lẫn Ảnh)
)
public class UploadCVServlet extends HttpServlet {
    private final String BUCKET_NAME = "other-project";
    private final CVDAO cvdao = new CVDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // PHẢI dùng thiết lập này để nhận dữ liệu tiếng Việt từ Form không bị lỗi font
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        try {
            // 1. Lấy các thông tin dạng chữ từ form gửi lên
            String candidateIDParam = request.getParameter("candidate_id");
            String cvTitle = request.getParameter("cv_title");
            String description = request.getParameter("description");
            String versionParam = request.getParameter("version");

            if (candidateIDParam == null || cvTitle == null || cvTitle.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Lỗi: Thiếu thông tin candidate_id hoặc cv_title!");
                return;
            }
            int candidateID = Integer.parseInt(candidateIDParam);

            // Mặc định version là 1 nếu bỏ trống
            String version = (versionParam != null && !versionParam.trim().isEmpty()) ? versionParam : "1";

            LocalDateTime now = LocalDateTime.now();
            // SỬA LỖI: Đổi định dạng timestamp thành chuỗi liền nhau an toàn, không chứa ký tự / và :
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Kết nối MinIO

            MinioClient minioClient = MinIOConfig.getMinioClient();
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            // 2. Xử lý lấy file CV từ Request
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Lỗi: Vui lòng chọn file CV!");
                return;
            }

            // Kiểm tra định dạng file là pdf hay là xlsx
            String contentType = filePart.getContentType();
            if (contentType == null || (!contentType.equals("application/pdf")
                    && !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Lỗi: Chỉ chấp nhận file PDF và file Excel!");
                return;
            }

            String originalFilename = filePart.getSubmittedFileName();
            String objectName = "cv_" + timestamp + "_" + originalFilename;

            try (InputStream inputStream = filePart.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .stream(inputStream, filePart.getSize(), -1)
                                .contentType(contentType)
                                .build()
                );
            }

            // =========================================================================
            // 3. ĐOẠN VIẾT TIẾP: XỬ LÝ LƯU ẢNH AVATAR VÀO TRƯỜNG avatar_url
            // =========================================================================
            String avatarObjectName = null;
            // Ở form data, file ảnh được truyền thông qua key "avatar_url"
            Part avatarPart = request.getPart("avatar_url");

            if (avatarPart != null && avatarPart.getSize() > 0) {
                // Đảm bảo file tải lên thực sự là ảnh (jpeg, png, gif...)
                String avatarContentType = avatarPart.getContentType();
                if (avatarContentType != null && avatarContentType.startsWith("image/")) {

                    String originalAvatarName = avatarPart.getSubmittedFileName();
                    avatarObjectName = "avatar_" + timestamp + "_" + originalAvatarName;

                    try (InputStream avatarStream = avatarPart.getInputStream()) {
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(BUCKET_NAME)
                                        .object(avatarObjectName)
                                        .stream(avatarStream, avatarPart.getSize(), -1)
                                        .contentType(avatarContentType)
                                        .build()
                        );
                    }
                }
            }

            // =========================================================================
            // 4. ĐÓNG GÓI TOÀN BỘ CÁC TRƯỜNG VÀO ĐỐI TƯỢNG MODEL CV
            // =========================================================================
            CV cv = new CV();

            // Khởi tạo đối tượng Candidates để set vào khóa ngoại của CV
            Candidates candidates = new Candidates();
            candidates.setId(candidateID);
            cv.setCandidateId(candidates);

            cv.setCvTitle(cvTitle);
            cv.setFileUrl(objectName);          // Đường dẫn file CV trên MinIO
            cv.setAvatarURl(avatarObjectName);  // Đường dẫn file Ảnh đại diện trên MinIO (null nếu không nộp)
            cv.setDescription(description != null ? description : "");
            cv.setVersion(version);
            cv.setCreatedAt(now);
            cv.setUpdatedAt(now);

            // =========================================================================
            // 5. GỌI HÀM add() CỦA CVDAO ĐỂ LƯU XUỐNG DATABASE
            // =========================================================================
            cvdao.add(cv);

            // Phản hồi kết quả tốt đẹp về giao diện
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Thành công: Đã upload CV lên MinIO và lưu thông tin vào cơ sở dữ liệu!\n" +
                    "- Tên file CV: " + objectName + "\n" +
                    "- Tên file Ảnh: " + (avatarObjectName != null ? avatarObjectName : "Không tải lên"));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Lỗi hệ thống Servlet: " + e.getMessage());
        }
    }
}