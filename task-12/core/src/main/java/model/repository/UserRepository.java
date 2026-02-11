package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import model.entity.DTO.UserProfile;
import model.entity.User;
import model.service.CSVHandler.UserCSVHandler;
import model.service.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IRepository<User> {
    private static final Logger logger = LogManager.getLogger();
    private final UserContext userContext;

    @Lazy
    private final UserCSVHandler csvHandler;

    @PersistenceContext
    private EntityManager em;

    private static final String GET_BY_ID_SUCCESS_MSG = "Пользователь с id = {} получен";
    private static final String GET_BY_ID_ERROR_MSG = "Не удалось получить данные пользователя с id = {}";
    private static final String GET_BY_NAME_SUCCESS_MSG = "Пользователь с name = {} получен";
    private static final String GET_BY_NAME_ERROR_MSG = "Не удалось получить данные пользователя с name = {}";
    private static final String GET_ALL_SUCCESS_MSG = "Список пользователей успешно получен.";
    private static final String ADD_SUCCESS_MSG = "Пользователь '{}' успешно добавлен";
    private static final String DELETE_BY_ID_SUCCESS_MSG = "Пользователь с id = {} успешно удален";
    private static final String DELETE_BY_ID_ERROR_MSG = "Не удалось получить данные пользователя с id = {}";
    private static final String IMPORT_SUCCESS_MSG = "Пользователи успешно импортированы из файла '{}'";

    public UserRepository(UserContext userContext, UserCSVHandler csvHandler) {
        this.userContext = userContext;
        this.csvHandler = csvHandler;
    }

    @Override
    public Optional<User> findById(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            logger.info(GET_BY_ID_SUCCESS_MSG, id);
            return Optional.of(user);
        } else {
            logger.error(GET_BY_ID_ERROR_MSG, id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("select u from User u", User.class);
        logger.info(GET_ALL_SUCCESS_MSG);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(User obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info(ADD_SUCCESS_MSG, obj.getName());
    }

    @Override
    @Transactional
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

    public User getCurrentUserProfileReference() {
        return em.getReference(User.class, userContext.getCurrentUser().getId());
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
        return user.getPassword().equals(password);
    }

    public void exportToCSV(String filePath) {
        csvHandler.exportToCSV(findAll(), filePath);
    }

    @Transactional
    public void importFromCSV(String filePath) {
        for (User user : csvHandler.importFromCSV(filePath)) {
            em.createNativeQuery("insert into users (" +
                    "id, " +
                    "name, " +
                    "password, " +
                    "role) " +
                    "values (:id, :name, :password, :role) " +
                    "on conflict (id) " +
                    "do update set " +
                    "name = EXCLUDED.name, " +
                    "password = EXCLUDED.password, " +
                    "role = EXCLUDED.role")
                    .setParameter("id", user.getId())
                    .setParameter("name", user.getName())
                    .setParameter("password", user.getPassword())
                    .setParameter("role", user.getRole().toString())
                    .executeUpdate();
        }

        logger.info(IMPORT_SUCCESS_MSG, filePath);
    }
}
