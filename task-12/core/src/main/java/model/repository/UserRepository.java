package model.repository;

import model.config.DBConnection;
import model.entity.DTO.UserProfile;
import model.entity.User;
import model.service.CSVHandler.CSVHandlers;
import model.utils.EntityParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements Serializable, IRepository<User> {
    private static final Logger logger = LogManager.getLogger();
    private static UserRepository INSTANCE;

    public static UserRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (var stmt = DBConnection.getInstance().getConnection().createStatement()) {
            try (var rs = stmt.executeQuery("select " +
                    "id as user_id, " +
                    "name as user_name, " +
                    "password as user_password, " +
                    "role as user_role " +
                    "from users")) {
                while (rs.next()) {
                    try {
                        users.add(EntityParser.parseUser(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные пользователя не удалось извлечь из БД: {}", e.getMessage());
                    }
                }
            }
            logger.info("Список пользователей успешно получен.");
        } catch (SQLException e) {
            logger.error("Не удалось получить список пользователей: {}", e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<User> findById(int userId) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("select " +
                                "id as user_id, " +
                                "name as user_name, " +
                                "password as user_password, " +
                                "role as user_role " +
                                "from users " +
                                "where id = ?")) {
            stmt.setInt(1, userId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Пользователь с id = {} получен", userId);
                    try {
                        return Optional.of(EntityParser.parseUser(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные пользователя не удалось извлечь из БД: {}", e.getMessage());
                        return Optional.empty();
                    }
                } else {
                    logger.error("Не удалось получить данные пользователя с id = {}", userId);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить данные пользователя с id = {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserProfile> findProfileById(int userId) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("select " +
                        "id as user_id, " +
                        "name as user_name " +
                        "from users " +
                        "where id = ?")) {
            stmt.setInt(1, userId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Пользователь с id = {} успешно получен", userId);
                    try {
                        return Optional.of(EntityParser.parseUserProfile(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные пользователя не удалось извлечь из БД: {}", e.getMessage());
                        return Optional.empty();
                    }
                } else {
                    logger.error("Не удалось получить данные пользователя с id = {}", userId);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить данные пользователя с id = {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void save(User user) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into users (" +
                        "name, " +
                        "password, " +
                        "role)" +
                        "values (?, ?, ?)")) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());

            stmt.execute();
            logger.info("Пользователь с логином '{}' успешно добавлен", user.getName());
        } catch (SQLException e) {
            logger.error("Не удалось добавить пользователя с логином '{}': {}", user.getName(), e.getMessage());
        }
    }

    public Optional<User> findByName(String userName) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("select " +
                        "id as user_id, " +
                        "name as user_name, " +
                        "password as user_password, " +
                        "role as user_role " +
                        "from users " +
                        "where name = ?")) {
            stmt.setString(1, userName);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Пользователь с name = {} успешно получен", userName);
                    try {
                        return Optional.of(EntityParser.parseUser(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные пользователя не удалось извлечь из БД: {}", e.getMessage());
                        return Optional.empty();
                    }
                } else {
                    logger.error("Не удалось получить данные пользователя с name = {}", userName);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить данные пользователя с name = {}: {}", userName, e.getMessage());
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
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into users (" +
                        "id, " +
                        "name, " +
                        "password, " +
                        "role) " +
                        "values (?, ?, ?, ?) " +
                        "on conflict (id) " +
                        "do update set " +
                        "name = EXCLUDED.name, " +
                        "password = EXCLUDED.password, " +
                        "role = EXCLUDED.role")) {
            for (User user : CSVHandlers.users().importFromCSV(filePath)) {
                stmt.setInt(1, user.getId());
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole().toString());

                stmt.addBatch();
            }

            stmt.executeBatch();
            logger.info("Пользователи успешно импортированы из файла '{}'", filePath);
        } catch (SQLException e) {
            logger.error("Не удалось импортировать пользователей из файла '{}'. Подробнее: {}", filePath, e.getMessage());
        }
    }
}
