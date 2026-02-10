package controller;

import model.BookShop;
import view.Builder;

import java.util.InputMismatchException;

public final class MenuController {
    private static MenuController INSTANCE;
    private final Builder builder = new Builder();
    private final Navigator navigator = new Navigator();

    private MenuController() { }

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
        do {
            System.out.println("\n=============================================================================");
            navigator.printMenu();
            System.out.println("Выберите пункт меню: ");
            try {
                choice = BookShop.SCANNER.nextInt();
                navigator.navigate(choice);
            } catch (InputMismatchException e) {
                System.out.println("Неверный ввод. Введите число из списка пунктов меню.");
                BookShop.SCANNER.nextLine();
            }
        } while (true);
    }
}
