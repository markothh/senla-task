package model.service;

import jakarta.transaction.Transactional;
import model.entity.User;
import model.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private static final Logger logger = LogManager.getLogger();
    private final UserRepository userRepository;

    private static final String USER_ID_NOT_FOUND_ERROR_MSG = "Пользователь с id = {} не найден";
    private static final String USER_CREATED_SUCCESS_MSG = "Пользователь с id = {} успешно создан";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_ID_NOT_FOUND_ERROR_MSG));
    }

    public User getUserByName(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new NoSuchElementException(USER_ID_NOT_FOUND_ERROR_MSG));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User createUser(String username, String password) {
        User user = new User(username, password);
        userRepository.save(user);
        logger.info(USER_CREATED_SUCCESS_MSG, user.getId());
        return user;
    }

    public void exportUsers(OutputStream os) {
        userRepository.exportToCSV(os);
    }

    @Transactional
    public void importUsers(File file) {
        userRepository.importFromCSV(file);
    }
}
