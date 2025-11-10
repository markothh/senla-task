package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

public class RequestSortFieldMenu extends Menu {
    public RequestSortFieldMenu(Menu previousMenu) {
        super(previousMenu);
        name = "Выбор поля для сортировки заказов";
        menuItems = new MenuItem[] {
                new MenuItem("Количество", () -> {}, new RequestSortOrderMenu("quantity", this)),
                new MenuItem("Название", () -> {}, new RequestSortOrderMenu("bookName", this))
        };
    }
}
