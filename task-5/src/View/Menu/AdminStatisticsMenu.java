package View.Menu;

import View.MenuItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AdminStatisticsMenu extends Menu {
    public AdminStatisticsMenu(Menu previousMenu) {
        super(previousMenu);
        this.name = "Просмотр статистики";
        this.menuItems = new MenuItem[] {
                new MenuItem("Просмотр выполненных заказов за период", () -> {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Введите начало периода (формат dd.MM.yyyy): ");
                    LocalDateTime startDate = LocalDateTime.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println("Введите конец периода (формат dd.MM.yyyy): ");
                    LocalDateTime endDate = LocalDateTime.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println(bookShop.getCompletedOrdersByPeriod(startDate, endDate));
                }, null),
                new MenuItem("Просмотр выручки за период", () -> {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Введите начало периода (формат dd.MM.yyyy): ");
                    LocalDateTime startDate = LocalDateTime.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println("Введите конец периода (формат dd.MM.yyyy): ");
                    LocalDateTime endDate = LocalDateTime.parse(scanner.next(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println(bookShop.getIncomeByPeriod(startDate, endDate));
                }, null),
                new MenuItem("Просмотр списка залежавшихся книг", () -> {
                    System.out.println(bookShop.getOverstockedBooks());
                }, null)
        };
    }
}
