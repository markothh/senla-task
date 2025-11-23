package Model.Repository;

import Model.Entity.User;
import Model.Service.CSVHandler.CSVHandlers;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

public class UserRepository implements Serializable {
    private static UserRepository INSTANCE;
    private List<User> users = new ArrayList<>();

    public static UserRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUserById(int userId) {
        return users.stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(
                        () -> {
                            String errMessage = String.format("Пользователь с id = %s не найден", userId);
                            Logger.getGlobal().severe(errMessage);
                            return new NoSuchElementException(errMessage);
                        }
                );
    }

    public User getUserByName(String name) {
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElseThrow(
                        () -> {
                            String errMessage = String.format("Пользователь с именем \"%s\" не найден", name);
                            Logger.getGlobal().severe(errMessage);
                            return new NoSuchElementException(errMessage);
                        }
                );
    }

    public void setData(List<User> users) {
        this.users = users;
    }

    public void authorize(User user, String password) {
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Вход не выполнен. Неверный пароль.");
        }
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.users().exportToCSV(filePath);
    }

    public void importToCSV (String filePath) {
        Map<Integer, User> merged = new HashMap<>();
        for (User user : users) {
            merged.put(user.getId(), user);
        }

        for (User user : CSVHandlers.users().importFromCSV(filePath)) {
            merged.put(user.getId(), user);
        }
        users = new ArrayList<>(merged.values());
    }

    @Serial
    private Object readResolve() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        else {
            INSTANCE.users = this.users;
        }
        return INSTANCE;
    }
}
