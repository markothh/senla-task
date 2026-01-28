package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

public class OrderSortOrderMenu extends Menu {
    public OrderSortOrderMenu(String field, Menu previousMenu) {
        super(previousMenu);
        this.field = field;
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String field;
    private final String NAME = "Выбор порядка сортировки заказов";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Прямой", () -> System.out.println(bookShop.getOrders(field, false)), null),
                new MenuItem("Обратный", () -> System.out.println(bookShop.getOrders(field, true)), null)
        };
    }
}
