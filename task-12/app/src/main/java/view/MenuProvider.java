package view;

import model.utils.Injector;
import view.menu.Menu;

public class MenuProvider {
    public static Menu create(Menu menu) {
        Injector.inject(menu);
        return menu;
    }
}
