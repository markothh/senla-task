package model.config;

import model.annotations.ConfigProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class DBConnection {
    private static DBConnection INSTANCE;

    @ConfigProperty(propertyName = "dbUrl")
    private static String URL;

    @ConfigProperty(propertyName = "dbUser")
    private static String USER;

    @ConfigProperty(propertyName = "dbPassword")
    private static String PASSWORD;

    private Connection connection;

    public static DBConnection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBConnection();
        }
        return INSTANCE;
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                String errMessage = "Не удалось установить соединение с БД";
                Logger.getGlobal().severe(errMessage);
                throw new RuntimeException(errMessage);
            }
        }
        return connection;
    }

    private DBConnection() { }
}
