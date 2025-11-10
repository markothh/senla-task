package View.Menu.Operations;

import Model.Enum.OrderStatus;
import View.Menu.Menu;
import View.MenuItem;

public class OrderChangeStatusMenu extends Menu {
    private final int orderId;
    public OrderChangeStatusMenu(int orderId, Menu previousMenu) {
        this.orderId = orderId;
        this.name = "Выбор нового статуса для заказа";
        this.menuItems = new MenuItem[] {
                new MenuItem("Новый", () -> bookShop.setOrderStatus(orderId, OrderStatus.NEW), null),
                new MenuItem("Отменен", () -> bookShop.setOrderStatus(orderId, OrderStatus.CANCELLED), null),
                new MenuItem("Выполнен", () -> bookShop.setOrderStatus(orderId, OrderStatus.COMPLETED), null),
        };
    }
}
