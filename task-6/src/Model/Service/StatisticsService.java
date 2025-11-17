package Model.Service;

import Model.Entity.Book;
import Model.Entity.Order;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class StatisticsService {
    private final RequestService requestService;
    private final OrderService orderService;
    private final BookService bookService;

    public StatisticsService(RequestService requestService, OrderService orderService, BookService bookService) {
        this.requestService = requestService;
        this.orderService = orderService;
        this.bookService = bookService;
    }

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

    public List<Book> getOverstockedBooks( int monthsToStayInStock) {
        return bookService.getBooks().stream()
                .filter(b -> b.getStockDate() != null)
                .filter(b -> b.getStockDate().isBefore(LocalDate.now().minusMonths(monthsToStayInStock)))
                .sorted(Comparator.comparing(Book::getStockDate)
                                  .thenComparing(Book::getPrice))
                .toList();
    }


}
