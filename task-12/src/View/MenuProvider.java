package View;

import Model.Utils.Injector;
import View.Menu.Menu;

public class MenuProvider {
    public static Menu create(Menu menu) {
        Injector.inject(menu);
        return menu;
    }
}
