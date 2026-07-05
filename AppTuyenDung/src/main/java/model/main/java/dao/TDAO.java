package model.main.java.dao;

public interface TDAO<T>extends IDAO<T> {
    T getById(int id);
}
