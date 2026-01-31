package View;

import View.Menu.Menu;

public interface MenuFactory {
    Menu createMainMenu();
    Menu createUserMenu(Menu previousMenu);
    Menu createBookMenu(Menu previousMenu);
    Menu createOrderMenu(Menu previousMenu);
    Menu createRequestMenu(Menu previousMenu);
}
