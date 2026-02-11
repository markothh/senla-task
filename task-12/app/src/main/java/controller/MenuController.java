package controller;

import model.BookShop;
import org.springframework.stereotype.Controller;
import view.Builder;

import java.util.InputMismatchException;

@Controller
public class MenuController {
    private final Builder builder;
    private final Navigator navigator;

    private MenuController(Builder builder, Navigator navigator) {
        this.builder = builder;
        this.navigator = navigator;
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
