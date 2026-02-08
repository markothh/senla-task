package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.entity.DTO.UserProfile;
import model.entity.User;
import model.service.CSVHandler.CSVHandlers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserRepository implements IRepository<User> {
    private static final Logger logger = LogManager.getLogger();
    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<User> findById(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            logger.info("Пользователь с id = {} получен", id);
            return Optional.of(user);
        } else {
            logger.error("Не удалось получить данные пользователя с id = {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        logger.info("Список пользователей успешно получен.");
        return query.getResultList();
    }

    @Override
    public void save(User obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info("Пользователь успешно добавлен");
    }

    @Override
    public void deleteById(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        } else {
            logger.error("Не удалось получить данные пользователя с id = {}", id);
        }
        logger.info("Пользователь с id = {} успешно удален", id);
    }

    public Optional<UserProfile> findProfileById(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            logger.info("Пользователь с id = {} получен", id);
            return Optional.of(new UserProfile(user.getId(), user.getName()));
        } else {
            logger.error("Не удалось получить данные пользователя с id = {}", id);
            return Optional.empty();
        }
    }

    public Optional<User> findByName(String name) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.name = :name", User.class);
        query.setParameter("name", name);
        User user = query.getSingleResult();
        if (user != null) {
            logger.info("Пользователь с name = {} получена", name);
            return Optional.of(user);
        } else {
            logger.error("Не удалось получить данные пользователя с name = {}", name);
            return Optional.empty();
        }
    }

    public boolean authorize(User user, String password) {
        return !user.getPassword().equals(password);
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.users().exportToCSV(filePath);
    }

    public void importFromCSV(String filePath) {
        for (User user : CSVHandlers.users().importFromCSV(filePath)) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                save(user);
                tx.commit();
            } catch (Exception e) {
                logger.error("Не удалось импортировать пользователя с id = {}: {}", user.getId(), e.getMessage());
                tx.rollback();
            }
        }

        logger.info("Пользователи успешно импортированы из файла '{}'", filePath);
    }
}
