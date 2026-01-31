package View.Menu;

import Model.Service.UserContext;
import View.MenuItem;
import View.Menu.Operations.OrderActionsMenu;
import View.Menu.Operations.OrderSortFieldMenu;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdminOrderMenu extends Menu {
    public AdminOrderMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Управление заказами";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Создать заказ", () -> {
                    System.out.println("Введите список книг, которые хотите заказать, через запятую в одну строку");
                    List<String> books = Arrays.asList(new Scanner(System.in).nextLine()
                            .split(","));
                    bookShop.createOrder(UserContext.getInstance().getCurrentUser(), books);
                },
                        null),
                new MenuItem("Выполнить действие с заказом по номеру", () -> {}, new OrderActionsMenu(this)),
                new MenuItem("Просмотреть все заказы", () -> {},
                        new OrderSortFieldMenu(this)),
                new MenuItem("Экспорт в CSV", () -> {
                    System.out.println("Введите путь к файлу для экспорта");
                    bookShop.exportOrders(new Scanner(System.in).nextLine());
                },
                        null),
                new MenuItem("Импорт из CSV", () -> {
                    System.out.println("Введите путь к файлу для импорта");
                    bookShop.importOrders(new Scanner(System.in).nextLine());
                },
                        null)
        };
    }
}
