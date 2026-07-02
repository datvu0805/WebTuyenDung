package dao;

import java.util.List;

public interface IDAO<T> {
    void add(T entity);
    void update(T entity);
    void delete(int id);
    T getById(int id);
    List<T> getAll();
}
