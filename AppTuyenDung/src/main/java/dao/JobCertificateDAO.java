package dao;

import config.DatabaseConfig;
import mapper.CertificateMapper;
import mapper.JobMapper;
import model.Certificate;
import model.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JobCertificateDAO extends DatabaseConfig {

    public void add(Job job, Certificate certificate) {

        String sql = "INSERT INTO job_certificates(job_id, certificate_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());
            ps.setInt(2, certificate.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addBatch(Job job, List<Certificate> certificates) {

        if (certificates == null || certificates.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO job_certificates(job_id, certificate_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Certificate certificate : certificates) {

                ps.setInt(1, job.getId());
                ps.setInt(2, certificate.getId());

                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Job job, Certificate certificate) {

        String sql = "DELETE FROM job_certificates WHERE job_id = ? AND certificate_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());
            ps.setInt(2, certificate.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByJob(Job job) {

        String sql = "DELETE FROM job_certificates WHERE job_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Certificate> getCertificatesByJob(Job job) {

        String sql = """
                SELECT c.*
                FROM certificates c
                INNER JOIN job_certificates jc
                    ON c.id = jc.certificate_id
                WHERE jc.job_id = ?
                """;

        List<Certificate> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(CertificateMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Job> getJobsByCertificate(Certificate certificate) {

        String sql = """
                SELECT j.*
                FROM jobs j
                INNER JOIN job_certificates jc
                    ON j.id = jc.job_id
                WHERE jc.certificate_id = ?
                """;

        List<Job> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, certificate.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(JobMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean exists(Job job, Certificate certificate) {

        String sql = """
                SELECT 1
                FROM job_certificates
                WHERE job_id = ?
                AND certificate_id = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, job.getId());
            ps.setInt(2, certificate.getId());

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}