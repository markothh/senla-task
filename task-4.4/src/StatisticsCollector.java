import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StatisticsCollector {
    private final RequestManager requestManager;
    private final OrderManager orderManager;
    private final Warehouse warehouse;

    public StatisticsCollector(RequestManager requestManager, OrderManager orderManager, Warehouse warehouse) {
        this.requestManager = requestManager;
        this.orderManager = orderManager;
        this.warehouse = warehouse;
    }

    public List<Order> getCompletedOrdersByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return orderManager.getOrders().stream()
                .filter(o -> o.getCompletedAt() != null)
                .filter(o -> o.getCompletedAt().isBefore(endDate) && o.getCompletedAt().isAfter(startDate))
                .toList();
    }


    public double getIncomeByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return orderManager.getOrders().stream()
                .filter(o -> o.getCompletedAt() != null)
                .filter(o -> o.getCompletedAt().isBefore(endDate) && o.getCompletedAt().isAfter(startDate))
                .mapToDouble(Order::getSum)
                .sum();
    }

    public List<Book> getOverstockedBooks( int monthsToStayInStock) {
        return warehouse.getBooks().stream()
                .filter(b -> b.getStockDate() != null)
                .filter(b -> b.getStockDate().isBefore(LocalDateTime.now().minusMonths(monthsToStayInStock)))
                .sorted(Comparator.comparing(Book::getStockDate)
                                  .thenComparing(Book::getPrice))
                .toList();
    }


}
