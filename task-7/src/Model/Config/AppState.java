package Model.Config;

import Model.Repository.BookRepository;
import Model.Repository.OrderRepository;
import Model.Repository.RequestRepository;
import Model.Repository.UserRepository;
import Model.Service.UserContext;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class AppState implements Serializable {
    private static final HashMap<String, String> dataPaths = new AppConfig("config.properties").getDataFiles();

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

            String infoMessage = String.format("Данные из файла %s загружены. ", filePath);
            System.out.println(infoMessage);
            Logger.getGlobal().warning(infoMessage);
        }
        catch (IOException | ClassNotFoundException e) {
            String errMessage = String.format("Произошла ошибка при загрузке состояния программы. " +
                    "Работа будет продолжена с пустыми списками объектов." +
                    "Файл: %s", filePath);
            System.out.println(errMessage);
            Logger.getGlobal().warning(errMessage);
        }
    }

    public static void saveState() {
        saveItems(dataPaths.get("users"), UserRepository.getInstance());
        saveItems(dataPaths.get("books"), BookRepository.getInstance());
        saveItems(dataPaths.get("orders"), OrderRepository.getInstance());
        saveItems(dataPaths.get("requests"), RequestRepository.getInstance());
        saveItems(dataPaths.get("userContext"), UserContext.getInstance());
    }

    public static void loadState() {
        loadItems(dataPaths.get("users"));
        loadItems(dataPaths.get("books"));
        loadItems(dataPaths.get("orders"));
        loadItems(dataPaths.get("requests"));
        loadItems(dataPaths.get("userContext"));
    }
}
