package model.main.java.dao;

import model.main.java.config.DatabaseConfig;

import java.util.List;

public class MessageDAO extends DatabaseConfig implements IDAO<MessageDAO> {
    @Override
    public void add(MessageDAO entity) {

    }

    @Override
    public void update(MessageDAO entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<MessageDAO> getAll() {
        return List.of();
    }
}
