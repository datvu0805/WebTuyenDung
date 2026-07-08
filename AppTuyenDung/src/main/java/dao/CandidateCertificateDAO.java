package dao;


import config.DatabaseConfig;
import model.Certificate;

import java.util.List;

public class CandidateCertificateDAO extends DatabaseConfig implements TDAO<Certificate> {
    @Override
    public void add(Certificate entity) {

    }

    @Override
    public void update(Certificate entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Certificate getById(int id) {
        return null;
    }

    @Override
    public List<Certificate> getAll() {
        return List.of();
    }
}
