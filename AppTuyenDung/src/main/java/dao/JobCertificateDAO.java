package dao;

import config.DatabaseConfig;
import model.JobCertificate;

import java.util.List;

public class JobCertificateDAO extends DatabaseConfig implements TDAO<JobCertificate> {
    @Override
    public void add(JobCertificate entity) {

    }

    @Override
    public void update(JobCertificate entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public JobCertificate getById(int id) {
        return null;
    }

    @Override
    public List<JobCertificate> getAll() {
        return List.of();
    }
}
