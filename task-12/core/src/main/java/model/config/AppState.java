package model.config;

import model.annotations.ConfigProperty;
import model.service.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.Serializable;

public class AppState implements Serializable {
    private static final Logger logger = LogManager.getLogger();

    @ConfigProperty(propertyName = "userContext")
    private static String userContextPath;

    private static void saveItems(String filePath, Object object) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filePath))) {
            os.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadItems(String filePath) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(filePath))) {
            is.readObject();
            logger.info("Данные из файла {} загружены. ", filePath);
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Произошла ошибка при загрузке состояния программы. " +
                    "Работа будет продолжена с пустым списком объектов." +
                    "Файл: {}", filePath);
        }
    }

    public static void saveState() {
        saveItems(userContextPath, UserContext.getInstance());
    }

    public static void loadState() {
        loadItems(userContextPath);
    }
}