package View;

import View.Menu.Menu;

public interface MenuFactory {
    Menu createMainMenu();
    Menu createBookMenu(Menu previousMenu);
    Menu createOrderMenu(Menu previousMenu);
    Menu createRequestMenu(Menu previousMenu);
    Menu createStatisticsMenu(Menu previousMenu);
}
