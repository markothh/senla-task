package View.Menu;

import View.MenuItem;

public class UserMenu extends Menu {
    public UserMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Управление пользователями";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Авторизоваться",
                        () -> {
                            bookShop.SCANNER.nextLine();
                            System.out.println("Введите имя пользователя");
                            String username = bookShop.SCANNER.nextLine();
                            System.out.println("Введите пароль");
                            String password = bookShop.SCANNER.nextLine();
                            bookShop.login(username, password);
                        },
                        null),
                new MenuItem("Выйти из аккаунта", () -> bookShop.logout(),null),
                new MenuItem("Просмотреть список пользователей", () -> System.out.println(bookShop.getUsers()), null),
                new MenuItem("Экспорт в CSV", () -> {
                    System.out.println("Введите путь к файлу для экспорта");
                    bookShop.SCANNER.nextLine();
                    bookShop.exportUsers(bookShop.SCANNER.nextLine());
                },
                        null),
                new MenuItem("Импорт из CSV", () -> {
                    System.out.println("Введите путь к файлу для импорта");
                    bookShop.SCANNER.nextLine();
                    bookShop.importUsers(bookShop.SCANNER.nextLine());
                },
                        null)
        };
    }
}
