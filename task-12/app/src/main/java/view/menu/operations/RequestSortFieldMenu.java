package view.menu.operations;

import view.menu.Menu;
import view.MenuItem;
import view.MenuProvider;

public class RequestSortFieldMenu extends Menu {
    public RequestSortFieldMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор поля для сортировки заказов";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Количество", () -> { }, MenuProvider.create(new RequestSortOrderMenu("quantity", this))),
                new MenuItem("Название", () -> { }, MenuProvider.create(new RequestSortOrderMenu("bookName", this)))
        };
    }
}
