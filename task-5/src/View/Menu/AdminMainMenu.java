package View.Menu;

import View.MenuItem;

public class AdminMainMenu extends Menu {
    public AdminMainMenu() {
        name = "Главное меню";
        menuItems = new MenuItem[] {
            new MenuItem("Управление книгами", () -> {}, new AdminBookMenu(this)),
            new MenuItem("Управление заказами", () -> {}, new AdminOrderMenu(this)),
            new MenuItem("Управление запросами", () -> {}, new AdminRequestMenu(this)),
            new MenuItem("Просмотр статистики", () -> {}, new AdminStatisticsMenu(this))
        };
    }
}
