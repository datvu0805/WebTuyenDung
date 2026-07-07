package service;

import config.MinIOConfig;
import io.minio.*;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

public class FileService {

    private static final String BUCKET_NAME = "other-project";

    public String uploadImage(Part filePart, String folder, int userId) {
        try {
            if (filePart == null || filePart.getSize() == 0) {
                return null;
            }

            String contentType = filePart.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Chỉ được upload file ảnh");
            }

            return upload(filePart, folder, userId);

        } catch (Exception e) {
            throw new RuntimeException("Upload ảnh thất bại", e);
        }
    }

    public String uploadCV(Part filePart, int userId) {
        try {
            if (filePart == null || filePart.getSize() == 0) {
                return null;
            }

            String contentType = filePart.getContentType();

            if (!"application/pdf".equals(contentType)) {
                throw new RuntimeException("Chỉ được upload file PDF");
            }

            return upload(filePart, "cv", userId);

        } catch (Exception e) {
            throw new RuntimeException("Upload CV thất bại", e);
        }
    }

    private String upload(Part filePart, String folder, int userId)
            throws Exception {

        MinioClient client = MinIOConfig.getClient();

        boolean exists = client.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(BUCKET_NAME)
                        .build()
        );

        if (!exists) {
            client.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(BUCKET_NAME)
                            .build()
            );
        }

        String originalName = filePart.getSubmittedFileName();

        LocalDate today = LocalDate.now();

        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());

        String objectName =
                folder + "/"
                        + userId + "/"
                        + year + "/"
                        + month + "/"
                        + UUID.randomUUID() + "_"
                        + originalName;

        try (InputStream inputStream = filePart.getInputStream()) {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .stream(inputStream, filePart.getSize(), -1L)
                            .contentType(filePart.getContentType())
                            .build()
            );
        }

        return objectName;
    }

    public String getFileUrl(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            return null;
        }

        return "http://minio.103.216.117.40.nip.io/"
                + BUCKET_NAME
                + "/"
                + objectName;
    }
}