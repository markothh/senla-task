package controller;

import model.config.AppState;
import view.menu.Menu;
import view.MenuItem;

public class Navigator {
    private Menu currentMenu;

    public void printMenu() {
        MenuItem[] menuItems = currentMenu.getMenuItems();
        System.out.println(currentMenu.getName());
        for (int i = 0; i < menuItems.length; i++) {
            System.out.printf("%d. %s%n", i + 1, menuItems[i].getTitle());
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("0 - Выход");
        System.out.println("-1 - Назад");
    }

    public void navigate(Integer index) {
        if (index == 0) {
            AppState.saveState();
            System.exit(0);
        } else if (index == -1) {
            if (currentMenu.getPreviousMenu() != null) {
                setCurrentMenu(currentMenu.getPreviousMenu());
            } else {
                System.out.println("Предыдущего меню не существует.");
            }
        } else {
            try {
                MenuItem selectedItem = currentMenu.getMenuItems()[index - 1];
                selectedItem.doAction();

                Menu nextMenu = selectedItem.getNextMenu();
                if (nextMenu != null) {
                    setCurrentMenu(nextMenu);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Такого пункта меню не существует. Выберите пункт из списка");
            }
        }
    }

    public void setCurrentMenu(Menu currentMenu) {
        this.currentMenu = currentMenu;
    }
}
