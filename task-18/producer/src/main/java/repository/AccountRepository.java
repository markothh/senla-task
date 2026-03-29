package repository;

import entity.Account;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AccountRepository {

    @PersistenceContext
    private EntityManager em;

    public long count() {
        return em.createQuery("select count(a) from Account a", Long.class)
                .getSingleResult();
    }

    public void save(Account acc) {
        em.persist(acc);
    }

    public List<Long> findAllIds() {
        return em.createQuery("select a.id from Account a", Long.class)
                .getResultList();
    }
}