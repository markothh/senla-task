package view.menu.operations;

import view.menu.Menu;
import view.MenuItem;

public class RequestSortOrderMenu extends Menu {
    public RequestSortOrderMenu(String field, Menu previousMenu) {
        super(previousMenu);
        this.field = field;
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String field;
    private final String NAME = "Выбор порядка сортировки запросов";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Прямой", () -> System.out.println(bookShop.getRequests(field, false)), null),
                new MenuItem("Обратный", () -> System.out.println(bookShop.getRequests(field, true)), null)
        };
    }
}
