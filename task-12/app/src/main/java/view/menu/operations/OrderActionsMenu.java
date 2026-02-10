package view.menu.operations;

import view.menu.Menu;
import view.MenuItem;


public class OrderActionsMenu extends Menu {
    private int orderId;
    public OrderActionsMenu(Menu previousMenu) {
        super(previousMenu);

        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Выбор действия с заказом";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Отменить заказ", () -> {
                    System.out.println("Введите номер заказа");
                    this.orderId = bookShop.SCANNER.nextInt();
                    bookShop.cancelOrder(orderId);
                }, null),
                new MenuItem("Изменить статус заказа", () -> {
                    System.out.println("Введите номер заказа");
                    this.orderId = bookShop.SCANNER.nextInt();
                }, new OrderChangeStatusMenu(orderId, this)),
                new MenuItem("Просмотреть детали заказа", () -> {
                    System.out.println("Введите номер заказа");
                    this.orderId = bookShop.SCANNER.nextInt();
                    System.out.println(bookShop.getOrderDetails(orderId));
                }, null)
        };
    }
}
