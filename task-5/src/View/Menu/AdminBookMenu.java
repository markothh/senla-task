package View.Menu;

import View.Menu.Operations.BookSortFieldMenu;
import View.MenuItem;

import java.util.Scanner;

public class AdminBookMenu extends Menu {
    public AdminBookMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = "Управление книгами";
        this.menuItems = new MenuItem[] {
                new MenuItem("Добавить книгу на склад",
                        () -> {
                            System.out.println("Введите название книги для добавления на склад");
                            bookShop.addBookToStock(new Scanner(System.in).nextLine());
                },
                        null),
                new MenuItem("Списать книгу со склада", () -> {
                    System.out.println("Введите название книги для списания книги со склада");
                    bookShop.removeBookFromStock(new Scanner(System.in).nextLine());
                },
                        null),
                new MenuItem("Просмотреть список книг", () -> {}, new BookSortFieldMenu(this)),
        };
    }
}
