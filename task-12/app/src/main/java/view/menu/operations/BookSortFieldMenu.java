package view.menu.operations;

import view.MenuProvider;
import view.menu.Menu;
import view.MenuItem;

public class BookSortFieldMenu extends Menu {
    public BookSortFieldMenu(Menu previousMenu) {
        super(previousMenu);
        name = NAME;
        menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор поля для сортировки книг";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Название", () -> { }, MenuProvider.create(new BookSortOrderMenu("bookName", this))),
                new MenuItem("Цена", () -> { }, MenuProvider.create(new BookSortOrderMenu("price", this))),
                new MenuItem("Дата издания", () -> { }, MenuProvider.create(new BookSortOrderMenu("publishDate", this))),
                new MenuItem("Наличие на складе", () -> { }, MenuProvider.create(new BookSortOrderMenu("stockAvailability", this)))
        };
    }
}