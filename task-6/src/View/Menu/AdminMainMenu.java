package View.Menu;

import View.MenuItem;

public class AdminMainMenu extends Menu {
    public AdminMainMenu() {
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Главное меню";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                    new MenuItem("Управление пользователями", () -> {}, new UserMenu(this)),
                    new MenuItem("Управление книгами", () -> {}, new AdminBookMenu(this)),
                    new MenuItem("Управление заказами", () -> {}, new AdminOrderMenu(this)),
                    new MenuItem("Управление запросами", () -> {}, new AdminRequestMenu(this)),
                    new MenuItem("Просмотр статистики", () -> {}, new AdminStatisticsMenu(this))
        };
    }
}
