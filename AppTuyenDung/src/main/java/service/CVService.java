package service;

import config.MinIOConfig;
import dao.CVDAO;
import dto.UploadCVDTO;
import io.minio.*;

import model.CV;
import model.Candidates;
import validator.CVValidator;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class CVService {
    private final String BUCKET_NAME = "other-project";
    private final CVDAO cvdao = new CVDAO();

    public CV handleUploadCV(UploadCVDTO dto) throws Exception {
        // 1. Kiểm tra dữ liệu đầu vào từ Validator
        String valResult = CVValidator.validate(dto);
        if (valResult != null) {
            throw new IllegalArgumentException(valResult);
        }

        int candidateID = Integer.parseInt(dto.getCandidateId());
        String version = (dto.getVersion() != null && !dto.getVersion().trim().isEmpty()) ? dto.getVersion() : "1";

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // 2. Kết nối MinIO và tự động tạo bucket nếu chưa có
        MinioClient minioClient = MinIOConfig.getClient();
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }

        // Đổi khoảng trắng trong tên file thành dấu gạch dưới để URL không bị lỗi ký tự %20
        String originalCvName = dto.getFileCV().getSubmittedFileName().replaceAll("\\s+", "_");
        String cvObjectName = "candidates/" + candidateID + "/cvs/cv_" + timestamp + "_" + originalCvName;

        // Đẩy file CV chính lên MinIO
        try (InputStream inputStream = dto.getFileCV().getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(BUCKET_NAME)
                            .object(cvObjectName).stream(inputStream, dto.getFileCV().getSize(), (long) -1)
                            .contentType(dto.getFileCV().getContentType()).build()
            );
        }

//        // Đẩy file Ảnh đại diện lên MinIO nếu có
//        String avatarObjectName = null;
//        if (dto.getFileAvatar() != null && dto.getFileAvatar().getSize() > 0) {
//            String originalAvatarName = dto.getFileAvatar().getSubmittedFileName().replaceAll("\\s+", "_");
//            avatarObjectName = "candidates/" + candidateID + "/avatars/avatar_" + timestamp + "_" + originalAvatarName;
//
//            try (InputStream avatarStream = dto.getFileAvatar().getInputStream()) {
//                minioClient.putObject(
//                        PutObjectArgs.builder().bucket(BUCKET_NAME)
//                                .object(avatarObjectName).stream(avatarStream, dto.getFileAvatar().getSize(), (long) -1)
//                                .contentType(dto.getFileAvatar().getContentType()).build()
//                );
//             }
//        }

        // 3. Khởi tạo thực thể Entity để chuẩn bị lưu xuống DB
        CV cv = new CV();
        Candidates candidates = new Candidates();
        candidates.setId(candidateID);
        cv.setCandidateId(candidates);

        cv.setCvTitle(dto.getCvTitle());

        //Chỉ lưu tên Object thô gọn gàng vào Database
        cv.setFileUrl(cvObjectName);
//        cv.setAvatarURl(avatarObjectName);

        cv.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        cv.setVersion(version);
        cv.setCreatedAt(now);
        cv.setUpdatedAt(now);

        // 4. BIẾN ĐỔI THÀNH LINK ĐỘNG (PRESIGNED URL) TRƯỚC KHI TRẢ VỀ POSTMAN
        // Bước này giúp đối tượng trả về Servlet chứa link full đầy đủ chìa khóa
        cv.setFileUrl(genarateMinioURL(cvObjectName));
//        cv.setAvatarURl(genarateMinioURL(avatarObjectName));
        // Gọi DAO lưu xuống PostgreSQL
        cvdao.add(cv);


        return cv;
    }


    public String genarateMinioURL(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            return null;
        }
        try {
            MinioClient minioClient = MinIOConfig.getClient();
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.GET) // Dùng phương thức GET để đọc file
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            System.err.println(">> [MINIO ERROR] Không thể ký URL cho object: " + objectName);
            e.printStackTrace();
            return null;
        }
    }
}