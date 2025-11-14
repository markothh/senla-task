package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

public class RequestSortFieldMenu extends Menu {
    public RequestSortFieldMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор поля для сортировки заказов";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Количество", () -> {}, new RequestSortOrderMenu("quantity", this)),
                new MenuItem("Название", () -> {}, new RequestSortOrderMenu("bookName", this))
        };
    }
}
