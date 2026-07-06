package dao;

import config.DatabaseConfig;
import model.JobCertificates;

import java.util.List;

public class JobCertificateDAO extends DatabaseConfig implements TDAO<JobCertificates> {
    @Override
    public void add(JobCertificates entity) {

    }

    @Override
    public void update(JobCertificates entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public JobCertificates getById(int id) {
        return null;
    }

    @Override
    public List<JobCertificates> getAll() {
        return List.of();
    }
}
