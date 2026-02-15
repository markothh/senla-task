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

    private static final String GET_BY_ID_SUCCESS_MSG = "Пользователь с id = {} получен";
    private static final String GET_BY_ID_ERROR_MSG = "Не удалось получить данные пользователя с id = {}";
    private static final String GET_BY_NAME_SUCCESS_MSG = "Пользователь с name = {} получен";
    private static final String GET_BY_NAME_ERROR_MSG = "Не удалось получить данные пользователя с name = {}";
    private static final String GET_ALL_SUCCESS_MSG = "Список пользователей успешно получен.";
    private static final String ADD_SUCCESS_MSG = "Пользователь '{}' успешно добавлена";
    private static final String DELETE_BY_ID_SUCCESS_MSG = "Пользователь с id = {} успешно удален";
    private static final String DELETE_BY_ID_ERROR_MSG = "Не удалось получить данные пользователя с id = {}";
    private static final String IMPORT_SUCCESS_MSG = "Пользователи успешно импортированы из файла '{}'";
    private static final String IMPORT_ERROR_MSG = "Не удалось импортировать пользователя с id = {}: {}";

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<User> findById(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            logger.debug(GET_BY_ID_SUCCESS_MSG, id);
            return Optional.of(user);
        } else {
            logger.error(GET_BY_ID_ERROR_MSG, id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        logger.debug(GET_ALL_SUCCESS_MSG);
        return query.getResultList();
    }

    @Override
    public void save(User obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info(ADD_SUCCESS_MSG);
    }

    @Override
    public void deleteById(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        } else {
            logger.error(DELETE_BY_ID_ERROR_MSG, id);
        }
        logger.info(DELETE_BY_ID_SUCCESS_MSG, id);
    }

    public Optional<UserProfile> findProfileById(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            logger.info(GET_BY_ID_SUCCESS_MSG, id);
            return Optional.of(new UserProfile(user.getId(), user.getName()));
        } else {
            logger.error(GET_BY_ID_ERROR_MSG, id);
            return Optional.empty();
        }
    }

    public Optional<User> findByName(String name) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.name = :name", User.class);
        query.setParameter("name", name);
        User user = query.getSingleResult();
        if (user != null) {
            logger.info(GET_BY_NAME_SUCCESS_MSG, name);
            return Optional.of(user);
        } else {
            logger.error(GET_BY_NAME_ERROR_MSG, name);
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
                logger.error(IMPORT_ERROR_MSG, user.getId(), e.getMessage());
                tx.rollback();
            }
        }

        logger.info(IMPORT_SUCCESS_MSG, filePath);
    }
}
