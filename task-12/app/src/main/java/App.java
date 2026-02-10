import controller.MenuController;

public class App {
    public static void main() {
        System.out.println(System.getProperty("user.dir"));

        MenuController menuController = MenuController.getInstance();
        menuController.run();
    }
}
