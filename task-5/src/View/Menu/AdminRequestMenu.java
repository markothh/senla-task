package View.Menu;

import View.MenuItem;
import View.Menu.Operations.RequestSortFieldMenu;

import java.util.Scanner;

public class AdminRequestMenu extends Menu {
    public AdminRequestMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = "Управление запросами";
        this.menuItems = new MenuItem[] {
                new MenuItem("Создать запрос на книгу", () -> {
                    System.out.println("Введите название книги, на которую хотите оставить запрос");
                    bookShop.createBookRequest(new Scanner(System.in).nextLine());
                }, null),
                new MenuItem("Просмотреть все активные запросы", () -> {}, new RequestSortFieldMenu(this))
        };
    }

}
