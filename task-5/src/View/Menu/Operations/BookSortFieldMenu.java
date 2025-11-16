package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

public class BookSortFieldMenu extends Menu {
    public BookSortFieldMenu(Menu previousMenu) {
        super(previousMenu);
        name = NAME;
        menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор поля для сортировки книг";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Название", () -> {}, new BookSortOrderMenu("bookName", this)),
                new MenuItem("Цена", () -> {}, new BookSortOrderMenu("price", this)),
                new MenuItem("Дата издания", () -> {}, new BookSortOrderMenu("publishDate", this)),
                new MenuItem("Наличие на складе", () -> {}, new BookSortOrderMenu("stockAvailability", this))
        };
    }
}
