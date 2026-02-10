package view.menu.operations;

import view.menu.Menu;
import view.MenuItem;

public class BookSortOrderMenu extends Menu {
    private final String field;

    public BookSortOrderMenu(String field, Menu previousMenu) {
        super(previousMenu);
        this.field = field;
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор порядка сортировки книг";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Прямой", () -> System.out.println(bookShop.getBooks(field, false)), null),
                new MenuItem("Обратный", () -> System.out.println(bookShop.getBooks(field, true)), null)
        };
    }
}
