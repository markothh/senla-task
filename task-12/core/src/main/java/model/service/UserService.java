package model.service;

import jakarta.transaction.Transactional;
import model.entity.DTO.UserProfile;
import model.entity.User;
import model.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LogManager.getLogger();
    private final UserRepository userRepository;
    private final UserContext userContext;

    private static final String LOGIN_SUCCESS_MSG = "Успешно выполенен вход. Текущий пользователь: {}, роль: {}";
    private static final String LOGIN_ERROR_MSG = "Вход не был выполнен. Неверный пароль.";
    private static final String USER_NOT_FOUND_ERROR_MSG = "Пользователь с логином '{}' не найден";
    private static final String LOGOUT_SUCCESS_MSG = "Выполнен выход из аккаунта. Текущий пользователь не инициализирован.";

    public UserService(UserRepository userRepository, UserContext userContext) {
        this.userRepository = userRepository;
        this.userContext = userContext;
    }

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
                                userContext.setCurrentUser(user);
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
        userContext.setCurrentUser(null);
        logger.info(LOGOUT_SUCCESS_MSG);
    }

    public int getCurrentUserId() {
        UserProfile cur = userContext.getCurrentUser();
        if (cur != null)
            return cur.getId();
        else return 0;
    }

    public void exportRequests(String filePath) {
        userRepository.exportToCSV(filePath);
    }

    @Transactional
    public void importRequests(String filePath) {
        userRepository.importFromCSV(filePath);
    }
}
