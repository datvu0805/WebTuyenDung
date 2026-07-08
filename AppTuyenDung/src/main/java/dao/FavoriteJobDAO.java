package dao;

import config.DatabaseConfig;
import model.FavoriteJob;

import java.util.List;

public class FavoriteJobDAO extends DatabaseConfig implements TDAO<FavoriteJob> {
    @Override
    public void add(FavoriteJob entity) {

    }

    @Override
    public void update(FavoriteJob entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public FavoriteJob getById(int id) {
        return null;
    }

    @Override
    public List<FavoriteJob> getAll() {
        return List.of();
    }
}
