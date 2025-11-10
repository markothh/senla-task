package Controller;

import View.Menu.Menu;
import View.MenuItem;

public class Navigator {
    private Menu currentMenu;

    public void printMenu() {
        MenuItem[] menuItems = currentMenu.getMenuItems();
        System.out.println(currentMenu.getName());
        for (int i = 0; i < menuItems.length; i++) {
            System.out.printf("%d. %s%n", i+1, menuItems[i].getTitle());
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("0 - Выход");
        System.out.println("-1 - Назад");
    };
    public void navigate(Integer index) {
        if (index == 0) {
            System.exit(0);
        } else if (index == -1) {
            setCurrentMenu(currentMenu.getPreviousMenu());
        } else {
            MenuItem selectedItem = currentMenu.getMenuItems()[index - 1];
            selectedItem.doAction();

            Menu nextMenu = selectedItem.getNextMenu();
            if (nextMenu != null) {
                setCurrentMenu(nextMenu);
            }
        }
    };

    public void setCurrentMenu(Menu currentMenu) {
        this.currentMenu = currentMenu;
    }
}
