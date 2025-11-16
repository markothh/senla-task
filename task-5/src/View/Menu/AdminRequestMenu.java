package View.Menu;

import View.MenuItem;
import View.Menu.Operations.RequestSortFieldMenu;

import java.util.Scanner;

public class AdminRequestMenu extends Menu {
    public AdminRequestMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generteMenuItems();
    }

    private final String NAME = "Управление запросами";

    private MenuItem[] generteMenuItems() {
        return new MenuItem[] {
                new MenuItem("Создать запрос на книгу", () -> {
                    System.out.println("Введите название книги, на которую хотите оставить запрос");
                    bookShop.createBookRequest(new Scanner(System.in).nextLine());
                }, null),
                new MenuItem("Просмотреть все активные запросы", () -> {}, new RequestSortFieldMenu(this))
        };
    }
}
