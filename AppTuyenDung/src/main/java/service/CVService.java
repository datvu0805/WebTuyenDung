package service;

import config.MinIOConfig;
import dao.CVDAO;
import dao.CandidateDAO;
import dao.IDAO;
import dto.UploadCVDTO;
import io.minio.*;
import model.CV;
import model.Candidates;
import validator.CVValidator;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CVService {
    private final String BUCKET_NAME = "other-project";
    private final IDAO dao;
    private final CandidateDAO candicateDAO = new CandidateDAO();

    public CVService(IDAO dao) {
        this.dao = dao;
    }

    public CV handleUploadCV(UploadCVDTO dto) throws Exception {
        String valResult = CVValidator.validate(dto);
        dao.delete();
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

        String originalCvName = dto.getFileCV().getSubmittedFileName().replaceAll("\\s+", "_");
        String cvObjectName = "candidates/" + candidateID + "/cvs/cv_" + timestamp + "_" + originalCvName;

        try (InputStream inputStream = dto.getFileCV().getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(BUCKET_NAME)
                            .object(cvObjectName).stream(inputStream, dto.getFileCV().getSize(), (long) -1)
                            .contentType(dto.getFileCV().getContentType()).build()
            );
        }

        CV cv = new CV();
        Candidates candidates = new Candidates();
        candidates.setId(candidateID);
        cv.setCandidateId(candidates);
        cv.setCvTitle(dto.getCvTitle());

        // GỢI Ý CHỮA CHÁY: Lưu object name thô vào DB trước rồi mới đổi link trả về client
        cv.setFileUrl(cvObjectName);
        cv.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        cv.setVersion(version);
        cv.setCreatedAt(now);
        cv.setUpdatedAt(now);

        cvdao.add(cv);

        // Ký link sau khi đã lưu DB thành công
        cv.setFileUrl(genarateMinioURL(cvObjectName));

        return cv;
    }

    // Lấy dữ liệu quản lý hồ sơ
    public Map<String, Object> getPureDashboarData(int candidateID){
        Candidates candidates = candicateDAO.getByID(candidateID);
        if(candidates == null) return null;

        if(candidates.getUser() != null && candidates.getUser().getAvatarUrl() != null){
            String rawAvatar = candidates.getUser().getAvatarUrl();
            if (!rawAvatar.trim().isEmpty()){
                candidates.getUser().setAvatarUrl(genarateMinioURL(rawAvatar));
            }
        }

        List<CV> cvList = cvdao.getCVByCandidateID(candidateID);

        if(cvList != null){
            for (CV cv : cvList){
                if (cv.getFileUrl() != null){
                    cv.setFileUrl(genarateMinioURL(cv.getFileUrl()));
                }
            }
        }

        Map<String, Object> reponesData = new HashMap<>();
        reponesData.put("candidateInfo", candidates);
        reponesData.put("cvList", cvList);

        return reponesData;
    }

    /**
     * XÓA CV TRONG QUẢN LÝ CV (Bản vá lỗi nuốt ngoại lệ và bao sân lỗi link tuyệt đối)
     */
    public boolean hanleDeleteCV(int cvId, int candidateID){
        try {
            CV cv = cvdao.getById(cvId);

            // Nếu không tìm thấy CV dưới DB xóa luôn phòng trường hợp getById lỗi
            if (cv == null){
                System.out.println(" Không tìm thấy object qua getById, ép xóa thẳng dưới DB.");
                cvdao.deleteID(cvId, candidateID);
                return true;
            }

            // Xóa file cv trên MinIO
            if (cv.getFileUrl() != null && !cv.getFileUrl().trim().isEmpty()){
                // Nếu đường dẫn lưu nhầm link tuyệt đối có HTTP, bỏ qua không xóa MinIO để tránh nổ lỗi crash code
                if (!cv.getFileUrl().startsWith("http://") && !cv.getFileUrl().startsWith("https://")) {
                    try {
                        MinioClient minioClient = MinIOConfig.getClient();
                        minioClient.removeObject(
                                RemoveObjectArgs.builder().bucket(BUCKET_NAME).object(cv.getFileUrl()).build()
                        );
                        System.out.println(">> Đã xóa file trên MinIO thành công.");
                    } catch (Exception minioE) {
                        System.out.println(">> Lỗi gỡ file MinIO (Bỏ qua để tiếp tục xóa DB): " + minioE.getMessage());
                    }
                }
            }

            // Thực thi xóa dòng dữ liệu trong PostgreSQL
            cvdao.deleteID(cvId, candidateID);
            System.out.println(">> Đã xóa bản ghi dưới DB thành công.");
            return true;

        } catch (Exception e) {
            System.err.println(">> Crash tại Service xử lý xóa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String genarateMinioURL(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            return null;
        }
        // Nếu bản thân nó đã là một URL ký sẵn rồi thì không ký đè nữa
        if (objectName.startsWith("http://") || objectName.startsWith("https://")) {
            return objectName;
        }
        try {
            MinioClient minioClient = MinIOConfig.getClient();
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.GET)
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            System.err.println(">> [MINIO ERROR] Không thể ký URL: " + objectName);
            return null;
        }
    }
}