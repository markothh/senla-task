package view.menu;

import view.MenuItem;
import view.MenuProvider;

public class AdminMainMenu extends Menu {
    public AdminMainMenu() {
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Главное меню";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                    new MenuItem("Управление пользователями", () -> { }, MenuProvider.create(new UserMenu(this))),
                    new MenuItem("Управление книгами", () -> { }, MenuProvider.create(new AdminBookMenu(this))),
                    new MenuItem("Управление заказами", () -> { }, MenuProvider.create(new AdminOrderMenu(this))),
                    new MenuItem("Управление запросами", () -> { }, MenuProvider.create(new AdminRequestMenu(this))),
                    new MenuItem("Просмотр статистики", () -> { }, MenuProvider.create(new AdminStatisticsMenu(this)))
        };
    }
}
