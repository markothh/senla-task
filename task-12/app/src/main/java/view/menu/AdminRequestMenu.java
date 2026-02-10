package view.menu;

import view.MenuItem;
import view.menu.operations.RequestSortFieldMenu;

import java.util.Scanner;

public class AdminRequestMenu extends Menu {
    public AdminRequestMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Управление запросами";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Создать запрос на книгу", () -> {
                    System.out.println("Введите название книги, на которую хотите оставить запрос");
                    bookShop.SCANNER.nextLine();
                    bookShop.createBookRequest(bookShop.SCANNER.nextLine());
                }, null),
                new MenuItem("Просмотреть все активные запросы", () -> { }, new RequestSortFieldMenu(this)),
                new MenuItem("Экспорт в CSV", () -> {
                    System.out.println("Введите путь к файлу для экспорта");
                    bookShop.SCANNER.nextLine();
                    bookShop.exportRequests(new Scanner(System.in).nextLine());
                },
                        null),
                new MenuItem("Импорт из CSV", () -> {
                    System.out.println("Введите путь к файлу для импорта");
                    bookShop.SCANNER.nextLine();
                    bookShop.importRequests(new Scanner(System.in).nextLine());
                },
                        null)
        };
    }
}
