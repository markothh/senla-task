package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.config.JPAConfig;
import model.entity.DTO.UserProfile;
import model.entity.Request;
import model.entity.User;
import model.service.CSVHandler.CSVHandlers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserRepository implements IRepository<User> {
    private static final Logger logger = LogManager.getLogger();
    private static UserRepository INSTANCE;

    public static UserRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
    }

    @Override
    public Optional<User> findById(int id) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            User user = em.find(User.class, id);
            if (user != null) {
                logger.info("Пользователь с id = {} получен", id);
                return Optional.of(user);
            } else {
                logger.error("Не удалось получить данные пользователя с id = {}", id);
                return Optional.empty();
            }
        }
    }

    @Override
    public List<User> findAll() {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            TypedQuery<User> query = em.createQuery("select u from User u", User.class);
            logger.info("Список пользователей успешно получен.");
            return query.getResultList();
        }
    }

    @Override
    public void save(EntityManager em, User obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info("Пользователь успешно добавлен");
    }

    @Override
    public void deleteById(EntityManager em, int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        } else {
            logger.error("Не удалось получить данные пользователя с id = {}", id);
        }
        logger.info("Пользователь с id = {} успешно удален", id);
    }

    public Optional<UserProfile> findProfileById(int id) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            User user = em.find(User.class, id);
            if (user != null) {
                logger.info("Пользователь с id = {} получен", id);
                return Optional.of(new UserProfile(user.getId(), user.getName()));
            } else {
                logger.error("Не удалось получить данные пользователя с id = {}", id);
                return Optional.empty();
            }
        }
    }

    public Optional<User> findByName(String name) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
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
    }

    public boolean authorize(User user, String password) {
        return !user.getPassword().equals(password);
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.users().exportToCSV(filePath);
    }

    public void importFromCSV(String filePath) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            for (User user : CSVHandlers.users().importFromCSV(filePath)) {
                EntityTransaction tx = em.getTransaction();
                try {
                    tx.begin();
                    save(em, user);
                    tx.commit();
                } catch (Exception e) {
                    logger.error("Не удалось импортировать пользователя с id = {}: {}", user.getId(), e.getMessage());
                    tx.rollback();
                }
            }
        }

        logger.info("Пользователи успешно импортированы из файла '{}'", filePath);
    }

    private UserRepository() { }
}
