package Model.Repository;

import Model.Config.DBConnection;
import Model.Entity.DTO.UserProfile;
import Model.Entity.User;
import Model.Service.CSVHandler.CSVHandlers;
import Model.Utils.EntityParser;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class UserRepository implements Serializable, IRepository<User> {
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
                    users.add(EntityParser.parseUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public User findById(int userId) {
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
                    return EntityParser.parseUser(rs);
                }
                else {
                    String errMessage = String.format("Не удалось получить данные пользователя с id=%s", userId);
                    Logger.getGlobal().severe(errMessage);
                    throw new RuntimeException(errMessage);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserProfile findProfileById(int userId) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("select " +
                        "id as user_id, " +
                        "name as user_name, " +
                        "from users " +
                        "where id = ?")) {
            stmt.setInt(1, userId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return EntityParser.parseUserProfile(rs);
                }
                else {
                    String errMessage = String.format("Не удалось получить данные пользователя с id=%s", userId);
                    Logger.getGlobal().severe(errMessage);
                    throw new RuntimeException(errMessage);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        } catch (SQLException e) {
            String errMessage = String.format("Не удалось добавить пользователя с логином '%s'", user.getName());
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
        }
    }

    public User findByName(String userName) {
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
                    return EntityParser.parseUser(rs);
                }
                else {
                    String errMessage = String.format("Не удалось получить данные пользователя с name=%s", userName);
                    Logger.getGlobal().severe(errMessage);
                    throw new RuntimeException(errMessage);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void authorize(User user, String password) {
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Вход не выполнен. Неверный пароль.");
        }
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.users().exportToCSV(filePath);
    }

    public void importFromCSV (String filePath) {
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
        } catch (SQLException e) {
            String errMessage = String.format("Не удалось импортировать пользователей: %s", e.getMessage());
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
        }
    }
}
