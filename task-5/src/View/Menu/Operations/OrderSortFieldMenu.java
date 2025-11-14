package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

public class OrderSortFieldMenu extends Menu {
    public OrderSortFieldMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор поля для сортировки книг";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Дата исполнения", () -> {}, new OrderSortOrderMenu("completedAt", this)),
                new MenuItem("Цена", () -> {}, new OrderSortOrderMenu("price", this)),
                new MenuItem("Статус", () -> {}, new OrderSortOrderMenu("status", this))
        };
    }
}
