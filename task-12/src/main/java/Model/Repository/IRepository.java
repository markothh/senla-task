package Model.Repository;

import java.util.List;

public interface IRepository<T> {
    public T findById(int id);
    public List<T> findAll();
    public void save(T obj);
}
