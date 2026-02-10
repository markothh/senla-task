package view.menu.operations;

import view.menu.Menu;
import view.MenuItem;
import view.MenuProvider;

public class OrderSortFieldMenu extends Menu {
    public OrderSortFieldMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор поля для сортировки книг";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Дата исполнения", () -> { }, MenuProvider.create(new OrderSortOrderMenu("completedAt", this))),
                new MenuItem("Цена", () -> { }, MenuProvider.create(new OrderSortOrderMenu("price", this))),
                new MenuItem("Статус", () -> { }, MenuProvider.create(new OrderSortOrderMenu("status", this)))
        };
    }
}
