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
    private static final String LOAD_SUCCESS_MSG = "Данные из файла {} загружены. ";
    private static final String LOAD_ERROR_MSG = "Произошла ошибка при загрузке состояния программы. " +
            "Работа будет продолжена с пустым списком объектов." +
            "Файл: {}";

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
            logger.info(LOAD_SUCCESS_MSG, filePath);
        } catch (IOException | ClassNotFoundException e) {
            logger.warn(LOAD_ERROR_MSG, filePath);
        }
    }

    public static void saveState() {
        saveItems(userContextPath, UserContext.getInstance());
    }

    public static void loadState() {
        loadItems(userContextPath);
    }
}