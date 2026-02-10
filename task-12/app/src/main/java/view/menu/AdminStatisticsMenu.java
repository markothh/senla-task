package view.menu;

import view.MenuItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminStatisticsMenu extends Menu {
    public AdminStatisticsMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = NAME;
        this.menuItems = generateMenuItems();
    }

    private final String NAME = "Просмотр статистики";

    private MenuItem[] generateMenuItems() {
        return new MenuItem[] {
                new MenuItem("Просмотр выполненных заказов за период", () -> {
                    System.out.println("Введите начало периода (формат dd.MM.yyyy): ");
                    LocalDate startDate = LocalDate.parse(bookShop.SCANNER.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println("Введите конец периода (формат dd.MM.yyyy): ");
                    LocalDate endDate = LocalDate.parse(bookShop.SCANNER.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println(bookShop.getCompletedOrdersByPeriod(startDate, endDate));
                }, null),
                new MenuItem("Просмотр выручки за период", () -> {
                    System.out.println("Введите начало периода (формат dd.MM.yyyy): ");
                    LocalDate startDate = LocalDate.parse(bookShop.SCANNER.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println("Введите конец периода (формат dd.MM.yyyy): ");
                    LocalDate endDate = LocalDate.parse(bookShop.SCANNER.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println(bookShop.getIncomeByPeriod(startDate, endDate));
                }, null),
                new MenuItem("Просмотр списка залежавшихся книг", () -> System.out.println(bookShop.getOverstockedBooks()), null)
        };
    }
}
