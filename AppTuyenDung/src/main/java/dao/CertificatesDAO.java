package dao;

import config.DatabaseConfig;
import model.Certificates;

import java.util.List;

public class CertificatesDAO extends DatabaseConfig implements IDAO<Certificates>{


    @Override
    public void add(Certificates entity) {

    }

    @Override
    public void update(Certificates entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Certificates getById(int id) {
        return null;
    }

    @Override
    public List<Certificates> getAll() {
        return List.of();
    }

}
