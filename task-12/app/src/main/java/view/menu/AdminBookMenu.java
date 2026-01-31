package view.menu;

import view.menu.operations.BookSortFieldMenu;
import view.MenuItem;

public class AdminBookMenu extends Menu {
    public AdminBookMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Управление книгами";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Добавить книгу на склад",
                        () -> {
                            System.out.println("Введите название книги для добавления на склад");
                            bookShop.SCANNER.nextLine();
                            bookShop.addBookToStock(bookShop.SCANNER.nextLine());
                        },
                        null),
                new MenuItem("Списать книгу со склада", () -> {
                    System.out.println("Введите название книги для списания книги со склада");
                    bookShop.SCANNER.nextLine();
                    bookShop.removeBookFromStock(bookShop.SCANNER.nextLine());
                },
                        null),
                new MenuItem("Просмотреть список книг", () -> { }, new BookSortFieldMenu(this)),
                new MenuItem("Экспорт в CSV", () -> {
                    System.out.println("Введите путь к файлу для экспорта");
                    bookShop.SCANNER.nextLine();
                    bookShop.exportBooks(bookShop.SCANNER.nextLine());
                },
                        null),
                new MenuItem("Импорт из CSV", () -> {
                    System.out.println("Введите путь к файлу для импорта");
                    bookShop.SCANNER.nextLine();
                    bookShop.importBooks(bookShop.SCANNER.nextLine());
                },
                        null)
        };
    }
}
