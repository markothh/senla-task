package View.Menu.Operations;

import View.Menu.Menu;
import View.MenuItem;

import java.util.Scanner;

public class OrderActionsMenu extends Menu {
    private int orderId;
    public OrderActionsMenu(Menu previousMenu) {
        super(previousMenu);
        Scanner scanner = new Scanner(System.in);
        this.name = "Выбор действия с заказом";
        this.menuItems = new MenuItem[] {
                new MenuItem("Отменить заказ", () -> {
                    System.out.println("Введите номер заказа");
                    this.orderId = scanner.nextInt();
                    bookShop.cancelOrder(orderId);
                }, null),
                new MenuItem("Изменить статус заказа", () -> {
                    System.out.println("Введите номер заказа");
                    this.orderId = scanner.nextInt();
                }, new OrderChangeStatusMenu(orderId, this)),
                new MenuItem("Просмотреть детали заказа", () -> {
                    System.out.println("Введите номер заказа");
                    this.orderId = scanner.nextInt();
                    System.out.println(bookShop.getOrderDetails(orderId));
                },null)
        };
    }
}
