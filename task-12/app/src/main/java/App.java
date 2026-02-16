import controller.MenuController;

public class App {
    public static void main() {
        MenuController menuController = MenuController.getInstance();
        menuController.run();
    }
}
