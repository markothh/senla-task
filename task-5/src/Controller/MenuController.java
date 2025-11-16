package Controller;

import View.Builder;

import java.util.Scanner;

public class MenuController {
    private static MenuController INSTANCE;
    private final Builder builder = new Builder();
    private final Navigator navigator = new Navigator();

    private MenuController() {}

    public static MenuController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MenuController();
        }
        return INSTANCE;
    }

    public void run() {
        builder.buildMenu();
        navigator.setCurrentMenu(builder.getRootMenu());

        int choice;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("\n=============================================================================");
            navigator.printMenu();
            System.out.println("Выберите пункт меню: ");
            choice = scanner.nextInt();
            navigator.navigate(choice);
        } while (true);
    }
}
