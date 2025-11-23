package Model.Service;

import Model.Entity.User;
import Model.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository = UserRepository.getInstance();

    public Optional<User> getUserById(int userId) {
        return userRepository.getUsers().stream()
                .filter(user -> user.getId() == userId)
                .findFirst();
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public void login (String username, String password) {
        try {
            User user = userRepository.getUserByName(username);
            userRepository.authorize(user, password);

            UserContext.getInstance().setCurrentUser(user);
            System.out.printf("Успешно выполенен вход. Текущий пользователь: %s, роль: %s", username, user.getRole());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void logout() {
        UserContext.getInstance().setCurrentUser(null);
        System.out.println("Выполнен выход из аккаунта. Текущий пользователь не инициализирован.");
    }

    public void exportRequests(String filePath) {
        userRepository.exportToCSV(filePath);
    }

    public void importRequests(String filePath) {
        userRepository.importToCSV(filePath);
    }
}
