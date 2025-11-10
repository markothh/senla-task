package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

public class RequestSortOrderMenu extends Menu {
    private final String field;

    public RequestSortOrderMenu(String field, Menu previousMenu) {
        super(previousMenu);
        this.field = field;
        this.name = "Выбор порядка сортировки запросов";
        this.menuItems = new MenuItem[] {
                new MenuItem("Прямой", () -> System.out.println(bookShop.getRequests(field, false)), null),
                new MenuItem("Обратный", () -> System.out.println(bookShop.getRequests(field, true)), null)
        };
    }
}
