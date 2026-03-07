package model.config;

import jakarta.annotation.PreDestroy;
import model.service.UserContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.Serializable;

@Component
public class AppState implements Serializable {
    private static final Logger logger = LogManager.getLogger();
    private final UserContext userContext;

    private static final String LOAD_SUCCESS_MSG = "Данные из файла {} загружены. ";
    private static final String LOAD_ERROR_MSG = "Произошла ошибка при загрузке состояния программы. " +
            "Работа будет продолжена с пустым списком объектов." +
            "Файл: {}";

    @Value("${userContext}")
    private String userContextPath;

    public AppState(UserContext userContext) {
        this.userContext = userContext;
    }

    private void saveItems(String filePath, Object object) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filePath))) {
            os.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadItems(String filePath) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(filePath))) {
            is.readObject();
            logger.info(LOAD_SUCCESS_MSG, filePath);
        } catch (IOException | ClassNotFoundException e) {
            logger.warn(LOAD_ERROR_MSG, filePath);
        }
    }

    @PreDestroy
    public void saveState() {
        saveItems(userContextPath, userContext);
    }

    public void loadState() {
        loadItems(userContextPath);
    }
}