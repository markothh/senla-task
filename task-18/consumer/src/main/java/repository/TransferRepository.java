package repository;

import entity.Transfer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class TransferRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Transfer transfer) {
        em.persist(transfer);
    }
}
