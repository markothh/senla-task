package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

public class OrderSortOrderMenu extends Menu {
    private final String field;

    public OrderSortOrderMenu(String field, Menu previousMenu) {
        super(previousMenu);
        this.field = field;
        this.name = "Выбор порядка сортировки заказов";
        this.menuItems = new MenuItem[] {
                new MenuItem("Прямой", () -> System.out.println(bookShop.getOrders(field, false)), null),
                new MenuItem("Обратный", () -> System.out.println(bookShop.getOrders(field, true)), null)
        };
    }
}
