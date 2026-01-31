package View.Menu;

import Model.Annotations.Inject;
import Model.BookShop;
import View.MenuItem;

public abstract class Menu {
    @Inject
    protected BookShop bookShop;
    protected String name;
    protected MenuItem[] menuItems;
    protected Menu previousMenu;

    public Menu() {
    }

    public Menu(Menu previousMenu) {
        this.previousMenu = previousMenu;
    }

    public MenuItem[] getMenuItems() {
        return menuItems;
    }

    public String getName() {
        return name;
    }

    public Menu getPreviousMenu() {
        return previousMenu;
    }
}
