package model.config;

import model.annotations.ConfigProperty;
import model.service.UserContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.logging.Logger;

public class AppState implements Serializable {
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
            Logger.getGlobal().info(String.format("Данные из файла %s загружены. ", filePath));
        } catch (IOException | ClassNotFoundException e) {
            Logger.getGlobal().warning(String.format("Произошла ошибка при загрузке состояния программы. " +
                    "Работа будет продолжена с пустым списком объектов." +
                    "Файл: %s", filePath));
        }
    }

    public static void saveState() {
        saveItems(userContextPath, UserContext.getInstance());
    }

    public static void loadState() {
        loadItems(userContextPath);
    }
}