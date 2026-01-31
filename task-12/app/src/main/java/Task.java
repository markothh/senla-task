import controller.MenuController;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Task {
    public static void main() {
        setupLogs();

        MenuController menuController = MenuController.getInstance();
        menuController.run();
    }
    private static void setupLogs() {
        try {
            FileHandler fileHandler = new FileHandler("bookshop.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            Logger logger = Logger.getGlobal();
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Ошибка настройки логирования");
        }
    }
}
