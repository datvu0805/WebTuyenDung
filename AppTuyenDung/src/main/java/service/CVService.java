package service;

import config.MinIOConfig;
import dao.CVDAO;
import dto.UploadCVDTO;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import model.CV;
import model.Candidates;
import validator.CVValidator;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CVService {
    private final String BUCKET_NAME = "other-project"; // Chuẩn S3 không chứa dấu gạch dưới (_)
    private final CVDAO cvdao = new CVDAO();

    public String handleUploadCV(UploadCVDTO dto) throws Exception {
        String valResult = CVValidator.validate(dto);
        if (valResult != null) {
            return valResult;
        }

        int candidateID = Integer.parseInt(dto.getCandidateId());
        String version = (dto.getVersion() != null && !dto.getVersion().trim().isEmpty()) ? dto.getVersion() : "1";

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        MinioClient minioClient = MinIOConfig.getClient();
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }

        String cvObjectName = "cv_" + timestamp + "_" + dto.getFileCV().getSubmittedFileName();
        try (InputStream inputStream = dto.getFileCV().getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(BUCKET_NAME)
                            .object(cvObjectName).stream(inputStream, dto.getFileCV().getSize(), (long) -1)
                            .contentType(dto.getFileCV().getContentType()).build()
            );
        }

        String avatarObjectName = null;
        if (dto.getFileAvatar() != null && dto.getFileAvatar().getSize() > 0) {
            avatarObjectName = "avatar_" + timestamp + "_" + dto.getFileAvatar().getSubmittedFileName();
            try (InputStream avatarStream = dto.getFileAvatar().getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder().bucket(BUCKET_NAME)
                                .object(avatarObjectName).stream(avatarStream, dto.getFileAvatar().getSize(), (long) -1)
                                .contentType(dto.getFileAvatar().getContentType()).build()
                );
            }
        }

        CV cv = new CV();
        Candidates candidates = new Candidates();
        candidates.setId(candidateID);
        cv.setCandidateId(candidates);

        cv.setCvTitle(dto.getCvTitle());
        cv.setFileUrl(cvObjectName);
        cv.setAvatarURl(avatarObjectName);
        cv.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        cv.setVersion(version);
        cv.setCreatedAt(now);
        cv.setUpdatedAt(now);

        cvdao.add(cv);
        return "SUCCESS";
    }
}