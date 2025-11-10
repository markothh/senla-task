import Controller.MenuController;
import Model.BookShop;
import Model.Entity.User;
import Model.Service.UserContext;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Task {
    public static void main() {
        setupLogs();

        UserContext.getInstance().setCurrentUser(new User("markothh"));
        BookShop bookShop = BookShop.getInstance();

        MenuController menuController = MenuController.getInstance();
        menuController.run();
    }

    private static void setupLogs() {
        try {
            FileHandler fileHandler = new FileHandler("bookshop.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            Logger logger = Logger.getGlobal();
            logger.addHandler(fileHandler);
        }
        catch (IOException e) {
            System.err.println("Ошибка настройки логирования");
        }
    }
}
