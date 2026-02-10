package view;

import view.menu.AdminMainMenu;
import view.menu.Menu;

public class Builder {
    private Menu rootMenu;

    public void buildMenu() {
        this.rootMenu = MenuProvider.create(new AdminMainMenu());
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
