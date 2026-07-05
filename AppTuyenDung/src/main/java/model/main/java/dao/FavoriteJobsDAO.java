package model.main.java.dao;

import model.main.java.config.DatabaseConfig;
import model.main.java.model.FavoriteJobs;

import java.util.List;

public class FavoriteJobsDAO extends DatabaseConfig implements TDAO<FavoriteJobs> {
    @Override
    public void add(FavoriteJobs entity) {

    }

    @Override
    public void update(FavoriteJobs entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public FavoriteJobs getById(int id) {
        return null;
    }

    @Override
    public List<FavoriteJobs> getAll() {
        return List.of();
    }
}
