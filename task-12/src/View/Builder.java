package View;

import View.Menu.AdminMainMenu;
import View.Menu.Menu;

public class Builder {
    private Menu rootMenu;

    public void buildMenu() {
        this.rootMenu = MenuProvider.create(new AdminMainMenu());
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
