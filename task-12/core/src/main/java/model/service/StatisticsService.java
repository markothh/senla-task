package model.service;

import model.entity.Book;
import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class StatisticsService {
    private final OrderService orderService;
    private final BookService bookService;

    @Value("${staleMonths}")
    private int staleMonths;

    public StatisticsService(BookService bookService, OrderService orderService) {
        this.bookService = bookService;
        this.orderService = orderService;
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

    public List<Book> getOverstockedBooks() {
        return bookService.getBooks().stream()
                .filter(b -> b.getStockDate() != null)
                .filter(b -> b.getStockDate().isBefore(LocalDate.now().minusMonths(staleMonths)))
                .sorted(Comparator.comparing(Book::getStockDate)
                                  .thenComparing(Book::getPrice))
                .toList();
    }
}
