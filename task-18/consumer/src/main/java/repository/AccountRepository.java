package repository;

import entity.Account;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class AccountRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(em.find(Account.class, id));
    }

    public void save(Account account) {
        em.merge(account);
    }
}
