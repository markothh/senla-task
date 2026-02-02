package model.config;

import model.annotations.ConfigProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final Logger logger = LogManager.getLogger();
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
                logger.fatal("Не удалось установить соединение с БД");
                throw new RuntimeException();
            }
        }
        return connection;
    }

    private DBConnection() { }
}
