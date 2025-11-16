package View.Menu;

import View.MenuItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

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
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Введите начало периода (формат dd.MM.yyyy): ");
                    LocalDate startDate = LocalDate.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println("Введите конец периода (формат dd.MM.yyyy): ");
                    LocalDate endDate = LocalDate.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println(bookShop.getCompletedOrdersByPeriod(startDate, endDate));
                }, null),
                new MenuItem("Просмотр выручки за период", () -> {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Введите начало периода (формат dd.MM.yyyy): ");
                    LocalDate startDate = LocalDate.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println("Введите конец периода (формат dd.MM.yyyy): ");
                    LocalDate endDate = LocalDate.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println(bookShop.getIncomeByPeriod(startDate, endDate));
                }, null),
                new MenuItem("Просмотр списка залежавшихся книг", () -> {
                    System.out.println(bookShop.getOverstockedBooks());
                }, null)
        };
    }
}
