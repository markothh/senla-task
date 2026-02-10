package view.menu;

import model.annotations.Inject;
import model.BookShop;
import view.MenuItem;

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
