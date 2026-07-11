package validator;

import dto.JobCertificateBatchDTO;
import dto.JobCertificateDTO;

public class JobCertificateValidator {

    public static void validate(JobCertificateDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống.");
        }

        if (dto.getJobId() == null || dto.getJobId() <= 0) {
            throw new IllegalArgumentException("Job không hợp lệ.");
        }

        if (dto.getCertificateId() == null || dto.getCertificateId() <= 0) {
            throw new IllegalArgumentException("Chứng chỉ không hợp lệ.");
        }
    }

    public static void validate(JobCertificateBatchDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu không được để trống.");
        }

        if (dto.getJobId() == null || dto.getJobId() <= 0) {
            throw new IllegalArgumentException("Job không hợp lệ.");
        }

        if (dto.getCertificateIds() == null || dto.getCertificateIds().isEmpty()) {
            throw new IllegalArgumentException("Danh sách chứng chỉ không được để trống.");
        }

        for (Integer id : dto.getCertificateIds()) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Chứng chỉ không hợp lệ.");
            }
        }
    }
}