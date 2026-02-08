package model.service;

import model.config.JPAConfig;
import model.entity.User;
import model.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class UserService {
    private static final Logger logger = LogManager.getLogger();
    private static UserService INSTANCE;
    private final UserRepository userRepository = new UserRepository(JPAConfig.getEntityManager());

    private static final String LOGIN_SUCCESS_MSG = "Успешно выполенен вход. Текущий пользователь: {}, роль: {}";
    private static final String LOGIN_ERROR_MSG = "Вход не был выполнен. Неверный пароль.";
    private static final String USER_NOT_FOUND_ERROR_MSG = "Пользователь с логином '{}' не найден";
    private static final String LOGOUT_SUCCESS_MSG = "Выполнен выход из аккаунта. Текущий пользователь не инициализирован.";

    public Optional<User> getUserById(int userId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getId() == userId)
                .findFirst();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void login(String username, String password) {
        userRepository.findByName(username)
                .ifPresentOrElse(
                        user -> {
                            if (userRepository.authorize(user, password)) {
                                UserContext.getInstance().setCurrentUser(user);
                                logger.info(LOGIN_SUCCESS_MSG, username, user.getRole());
                            } else {
                                logger.error(LOGIN_ERROR_MSG);
                            }
                        },
                        () -> {
                            logger.error(USER_NOT_FOUND_ERROR_MSG, username);
                        });

    }

    public void logout() {
        UserContext.getInstance().setCurrentUser(null);
        logger.info(LOGOUT_SUCCESS_MSG);
    }

    public void exportRequests(String filePath) {
        userRepository.exportToCSV(filePath);
    }

    public void importRequests(String filePath) {
        userRepository.importFromCSV(filePath);
    }

    public static UserService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
    }

    private UserService() { }
}
