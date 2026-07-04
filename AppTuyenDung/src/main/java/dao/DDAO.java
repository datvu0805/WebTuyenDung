package dao;

import model.Transactions;

public interface DDAO<T> extends IDAO<T>{
   T getByID(int id);
   T findByEmail(String email);
}
