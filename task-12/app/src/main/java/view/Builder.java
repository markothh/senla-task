package view;

import model.BookShop;
import org.springframework.stereotype.Component;
import view.menu.AdminMainMenu;
import view.menu.Menu;

@Component
public class Builder {
    private Menu rootMenu;

    public Builder(BookShop bookShop) {
        MenuProvider.setBookShop(bookShop);
    }

    public void buildMenu() {
        this.rootMenu = MenuProvider.create(new AdminMainMenu());
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
