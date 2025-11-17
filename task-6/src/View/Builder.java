package View;

import View.Menu.AdminConsoleMenuFactory;
import View.Menu.Menu;

public class Builder {
    private Menu rootMenu;
    MenuFactory menuFactory;

    public void buildMenu() {
        this.menuFactory = new AdminConsoleMenuFactory();
        this.rootMenu = menuFactory.createMainMenu();
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
