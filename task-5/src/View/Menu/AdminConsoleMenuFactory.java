package View.Menu;

import View.MenuFactory;

public class AdminConsoleMenuFactory implements MenuFactory {

    @Override
    public Menu createMainMenu() {
        return new AdminMainMenu();
    }

    @Override
    public Menu createBookMenu(Menu previousMenu) {
        return new AdminBookMenu(previousMenu);
    }

    @Override
    public Menu createOrderMenu(Menu previousMenu) {
        return new AdminOrderMenu(previousMenu);
    }

    public Menu createRequestMenu(Menu previousMenu) {
        return new AdminRequestMenu(previousMenu);
    }

    public Menu createStatisticsMenu(Menu previousMenu) {
        return new AdminStatisticsMenu(previousMenu);
    }

}
