package service;

import dao.CertificateDAO;
import dao.JobCertificateDAO;
import dao.JobDAO;
import dto.JobCertificateBatchDTO;
import dto.JobCertificateDTO;
import model.Certificate;
import model.Job;
import validator.JobCertificateValidator;

import java.util.ArrayList;
import java.util.List;

public class JobCertificateService {

    private final JobCertificateDAO jobCertificateDAO = new JobCertificateDAO();
    private final JobDAO jobDAO = new JobDAO();
    private final CertificateDAO certificateDAO = new CertificateDAO();

    /**
     * Thêm một chứng chỉ cho Job
     */
    public void add(JobCertificateDTO dto) {

        JobCertificateValidator.validate(dto);

        Job job = jobDAO.getById(dto.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        Certificate certificate = certificateDAO.getById(dto.getCertificateId());
        if (certificate == null) {
            throw new IllegalArgumentException("Chứng chỉ không tồn tại.");
        }

        if (jobCertificateDAO.exists(job, certificate)) {
            throw new IllegalArgumentException("Job đã yêu cầu chứng chỉ này.");
        }

        jobCertificateDAO.add(job, certificate);
    }

    /**
     * Thêm nhiều chứng chỉ
     */
    public void add(JobCertificateBatchDTO dto) {

        JobCertificateValidator.validate(dto);

        Job job = jobDAO.getById(dto.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        List<Certificate> certificates = new ArrayList<>();

        for (Integer id : dto.getCertificateIds()) {

            Certificate certificate = certificateDAO.getById(id);

            if (certificate == null) {
                throw new IllegalArgumentException("Chứng chỉ không tồn tại. ID = " + id);
            }

            certificates.add(certificate);
        }

        jobCertificateDAO.addBatch(job, certificates);
    }


    /**
     * Xóa một chứng chỉ khỏi Job
     */
    public void delete(JobCertificateDTO dto) {

        JobCertificateValidator.validate(dto);

        Job job = jobDAO.getById(dto.getJobId());
        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        Certificate certificate = certificateDAO.getById(dto.getCertificateId());
        if (certificate == null) {
            throw new IllegalArgumentException("Chứng chỉ không tồn tại.");
        }

        jobCertificateDAO.delete(job, certificate);
    }

    /**
     * Lấy danh sách chứng chỉ của Job
     */
    public List<Certificate> getCertificatesByJob(int jobId) {

        Job job = jobDAO.getById(jobId);

        if (job == null) {
            throw new IllegalArgumentException("Công việc không tồn tại.");
        }

        return jobCertificateDAO.getCertificatesByJob(job);
    }

    /**
     * Lấy danh sách Job theo chứng chỉ
     */
    public List<Job> getJobsByCertificate(int certificateId) {

        Certificate certificate = certificateDAO.getById(certificateId);

        if (certificate == null) {
            throw new IllegalArgumentException("Chứng chỉ không tồn tại.");
        }

        return jobCertificateDAO.getJobsByCertificate(certificate);
    }

    /**
     * Kiểm tra Job có yêu cầu chứng chỉ hay không
     */
    public boolean exists(int jobId, int certificateId) {

        Job job = jobDAO.getById(jobId);
        Certificate certificate = certificateDAO.getById(certificateId);

        if (job == null || certificate == null) {
            return false;
        }

        return jobCertificateDAO.exists(job, certificate);
    }
}