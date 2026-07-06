package dao;

import config.DatabaseConfig;
import model.Skills;

import java.util.List;

public class SkillDAO extends DatabaseConfig implements TDAO<Skills> {
    @Override
    public void add(Skills entity) {

    }

    @Override
    public void update(Skills entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Skills getById(int id) {
        return null;
    }

    @Override
    public List<Skills> getAll() {
        return List.of();
    }
}
