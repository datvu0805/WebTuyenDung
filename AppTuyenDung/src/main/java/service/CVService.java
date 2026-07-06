package service;

import config.MinIOConfig;
import dao.CVDAO;
import dto.UploadCVDTO;
import io.minio.*;
import io.minio.errors.MinioException;
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
        String valResult = CVValidator.validate(dto);
        if (valResult != null) {
           throw new IllegalArgumentException(valResult);
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

        String cvObjectName = "candidates/" +candidateID  + "/cvs/cv_" + timestamp + "_"+ dto.getFileCV().getSubmittedFileName();
        try (InputStream inputStream = dto.getFileCV().getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(BUCKET_NAME)
                            .object(cvObjectName).stream(inputStream, dto.getFileCV().getSize(), (long) -1)
                            .contentType(dto.getFileCV().getContentType()).build()
            );
        }

        String avatarObjectName = null;
        if (dto.getFileAvatar() != null && dto.getFileAvatar().getSize() > 0) {
            avatarObjectName = "candidates/" + candidateID + "/avatars/avatar_" + timestamp + "_" + dto.getFileAvatar().getSubmittedFileName();
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

        // chuyển đổi thành link url;
        cv.setFileUrl(genarateMinioURL(cvObjectName));
        cv.setAvatarURl(genarateMinioURL(avatarObjectName));
        return cv;
    }
    public String genarateMinioURL(String objectName){
        if(objectName == null || objectName.trim().isEmpty()){
            return null;
        }
        try{
            MinioClient minioClient = MinIOConfig.getClient();
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.GET)
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS).build()
            );
        } catch (MinioException e) {
            e.printStackTrace();
            return null;
        }
    }
}