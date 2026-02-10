package model.service;

import model.annotations.Inject;
import model.entity.Book;
import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public final class StatisticsService {
    private static final Logger logger = LogManager.getLogger();
    private static StatisticsService INSTANCE;
    @Inject
    private RequestService requestService;
    @Inject
    private OrderService orderService;
    @Inject
    private BookService bookService;

    public List<Order> getCompletedOrdersByPeriod(LocalDate startDate, LocalDate endDate) {
        return orderService.getOrders().stream()
                .filter(o -> o.getCompletedAt() != null)
                .filter(o -> o.getCompletedAt().isBefore(endDate) && o.getCompletedAt().isAfter(startDate))
                .toList();
    }


    public double getIncomeByPeriod(LocalDate startDate, LocalDate endDate) {
        return orderService.getOrders().stream()
                .filter(o -> o.getCompletedAt() != null)
                .filter(o -> o.getCompletedAt().isBefore(endDate) && o.getCompletedAt().isAfter(startDate))
                .mapToDouble(Order::getSum)
                .sum();
    }

    public List<Book> getOverstockedBooks(int monthsToStayInStock) {
        return bookService.getBooks().stream()
                .filter(b -> b.getStockDate() != null)
                .filter(b -> b.getStockDate().isBefore(LocalDate.now().minusMonths(monthsToStayInStock)))
                .sorted(Comparator.comparing(Book::getStockDate)
                                  .thenComparing(Book::getPrice))
                .toList();
    }

    public static StatisticsService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatisticsService();
        }
        return INSTANCE;
    }

    private StatisticsService() { }
}
