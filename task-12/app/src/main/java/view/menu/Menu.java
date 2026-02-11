package view.menu;

import model.BookShop;
import view.MenuItem;

public abstract class Menu {
    protected BookShop bookShop;
    protected String name;
    protected MenuItem[] menuItems;
    protected Menu previousMenu;

    public Menu() { }

    public Menu(Menu previousMenu) {
        this.previousMenu = previousMenu;
    }

    public void setBookShop(BookShop bookShop) {
        this.bookShop = bookShop;
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
