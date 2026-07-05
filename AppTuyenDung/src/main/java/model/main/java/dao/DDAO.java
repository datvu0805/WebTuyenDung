package model.main.java.dao;

public interface DDAO<T> extends IDAO<T> {
   T getByID(int id);
   T findByEmail(String email);
}
