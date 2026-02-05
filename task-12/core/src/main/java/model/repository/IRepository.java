package model.repository;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    void save(EntityManager em, T obj);
    void deleteById(EntityManager em, int id);
}
