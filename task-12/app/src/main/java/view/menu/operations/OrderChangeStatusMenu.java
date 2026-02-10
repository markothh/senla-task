package view.menu.operations;

import model.enums.OrderStatus;
import view.menu.Menu;
import view.MenuItem;

public class OrderChangeStatusMenu extends Menu {
    public OrderChangeStatusMenu(int orderId, Menu previousMenu) {
        super(previousMenu);
        this.orderId = orderId;
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final int orderId;
    private final String NAME = "Выбор нового статуса для заказа";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Новый", () -> bookShop.setOrderStatus(orderId, OrderStatus.NEW), null),
                new MenuItem("Отменен", () -> bookShop.setOrderStatus(orderId, OrderStatus.CANCELLED), null),
                new MenuItem("Выполнен", () -> bookShop.setOrderStatus(orderId, OrderStatus.COMPLETED), null),
        };
    }
}
