package dao;

import java.util.List;

public interface JPARepository<T, ID> {

    void add(T entity);

    void update(T entity);

    void delete(T entity);

    T findById(ID id);
    List<T> getAll();
}