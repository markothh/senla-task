package view;

import model.BookShop;
import view.menu.Menu;

public class MenuProvider {
    private static BookShop bookShop;

    public static void setBookShop(BookShop bookShop) {
        MenuProvider.bookShop = bookShop;
    }

    public static Menu create(Menu menu) {
        menu.setBookShop(bookShop);
        return menu;
    }
}
