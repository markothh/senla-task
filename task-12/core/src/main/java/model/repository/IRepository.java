package model.repository;

import java.util.List;

public interface IRepository<T> {
    T findById(int id);
    List<T> findAll();
    void save(T obj);
}
