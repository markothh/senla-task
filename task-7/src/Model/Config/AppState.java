package Model.Config;

import Model.Repository.BookRepository;
import Model.Repository.OrderRepository;
import Model.Repository.RequestRepository;
import Model.Repository.UserRepository;
import Model.Service.UserContext;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class AppState implements Serializable {
    private static final AppConfig appConfig = new AppConfig();

    static {
        new ConfigLoader().configure(appConfig);
    }

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
            Logger.getGlobal().warning(String.format("Данные из файла %s загружены. ", filePath));
        }
        catch (IOException | ClassNotFoundException e) {
            Logger.getGlobal().warning(String.format("Произошла ошибка при загрузке состояния программы. " +
                    "Работа будет продолжена с пустым списком объектов." +
                    "Файл: %s", filePath));
        }
    }

    public static void saveState() {
        ArrayList<String> dataPaths = appConfig.getDataFiles();
        saveItems(dataPaths.get(0), UserRepository.getInstance());
        saveItems(dataPaths.get(1), BookRepository.getInstance());
        saveItems(dataPaths.get(2), OrderRepository.getInstance());
        saveItems(dataPaths.get(3), RequestRepository.getInstance());
        saveItems(dataPaths.get(4), UserContext.getInstance());
    }

    public static void loadState() {
        ArrayList<String> dataPaths = appConfig.getDataFiles();
        loadItems(dataPaths.get(0));
        loadItems(dataPaths.get(1));
        loadItems(dataPaths.get(2));
        loadItems(dataPaths.get(3));
        loadItems(dataPaths.get(4));
    }
}
