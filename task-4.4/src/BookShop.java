import java.time.LocalDateTime;
import java.util.List;

public class BookShop {
    private final RequestManager requestManager = new RequestManager();
    private final OrderManager orderManager = new OrderManager(requestManager);
    private final Warehouse warehouse = new Warehouse();
    private final StatisticsCollector statistics = new StatisticsCollector(requestManager, orderManager, warehouse);

    //----------------------------------------Task 3--------------------------------------------
    public void removeBookFromStock(String bookName) {
        warehouse.removeFromStock(bookName);
    }

    public int createOrder(User user, List<String> bookNames) {
        return orderManager.createOrder(user, warehouse.formIdListFromNames(bookNames)).getId();
    }

    public void cancelOrder(int orderId) {
        orderManager.cancelOrder(orderId);
    }

    public void setOrderStatus(int orderId, String status) {
        orderManager.setOrderStatus(orderId, status);
    }

    public void addBookToStock(String bookName) {
        warehouse.addToStock(bookName);
        requestManager.satisfyAllRequestsByBookId(warehouse.getBookByName(bookName));
    }

    public int createBookRequest(String bookName) {
        if (warehouse.isBookAvailable(bookName)) {
            throw new IllegalArgumentException("Невозможно создать заказ на книгу, которая есть в наличии.");
        }

        return requestManager.createRequest(warehouse.getBookByName(bookName)).getId();
    }

    //----------------------------------------Task 4--------------------------------------------
    public List<Book> getBooks() {
        return warehouse.getBooks();
    };

    public List<Book> getBooks(String sortBy, boolean isReversed) {
        return warehouse.getSortedBooks(sortBy, isReversed);
    };

    public List<Order> getOrders() {
        return orderManager.getOrders();
    };

    public List<Order> getOrders(String sortBy, boolean isReversed) {
        return orderManager.getSortedOrders(sortBy, isReversed);
    };

    public List<Request> getRequests() {
        return requestManager.getRequests();
    };

    public List<Request> getRequests(String sortBy, boolean isReversed) {
        return requestManager.getSortedRequests(sortBy, isReversed);
    };

    public List<Order> getCompletedOrdersByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return statistics.getCompletedOrdersByPeriod(startDate, endDate);
    };

    public double getIncomeByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return statistics.getIncomeByPeriod(startDate, endDate);
    };

    public List<Book> getOverstockedBooks() {
        return statistics.getOverstockedBooks(6);
    };

    public String getOrderDetails(int orderId) {
        return orderManager.getOrderInfo(orderId);
    };

    public String getBookDescription(String bookName) {
        return warehouse.getDescriptionByBookName(bookName);
    };
}
