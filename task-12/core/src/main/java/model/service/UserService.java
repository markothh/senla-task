package model.service;

import jakarta.transaction.Transactional;
import model.entity.User;
import model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;

    private static final String USER_ID_NOT_FOUND_ERROR_MSG = "Пользователь с id = {} не найден";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_ID_NOT_FOUND_ERROR_MSG));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void exportUsers(OutputStream os) {
        userRepository.exportToCSV(os);
    }

    @Transactional
    public void importUsers(File file) {
        userRepository.importFromCSV(file);
    }
}
