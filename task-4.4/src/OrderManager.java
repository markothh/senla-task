import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderManager {
    private RequestManager requestManager;
    private List<Order> orders = new ArrayList<>();

    public OrderManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    private Order getOrderById(int orderId) {
        Optional<Order> order = orders.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst();

        if (order.isEmpty()) {
            throw new IllegalArgumentException("Заказ с указанным id не найден.");
        }

        return order.get();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Order> getSortedOrders(String sortBy, boolean isReversed) {
        HashMap<String, Comparator<Order>> comparators = new HashMap<>() {{
            put("completedAt", Comparator.comparing(Order::getCompletedAt, Comparator.nullsLast(LocalDateTime::compareTo)));
            put("price", Comparator.comparing(Order::getSum));
            put("status", Comparator.comparing(Order::getStatus));
        }};

        Comparator<Order> comparator = comparators.get(sortBy);
        if (comparator == null) {
                throw new IllegalArgumentException("Невозможна сортировка по указанному полю. " +
                        "Возможные значения параметра сортировки: completedAt, price, status");
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return orders.stream()
                .sorted(comparator)
                .toList();
    }

    public String getOrderInfo(int orderId) {
        return getOrderById(orderId).getInfo();
    }

    public void setOrderStatus(int orderId, String status) {
        getOrderById(orderId).setStatus(status);
    }

    public Order createOrder(User user, List<Book> books) {
        Order order = new Order(user);
        for (Book book : books) {
            if (!book.isAvailable()) {
                System.out.printf("%nКниги \'%s\' нет на складе. Создается запрос...", book.getName());
                requestManager.createRequest(book);
            }

            order.addBook(book);
        }

        orders.add(order);

        System.out.printf("%nСоздан заказ: %n%s", order);

        System.out.printf("");
        return order;
    }

    public void cancelOrder(int orderId) {
        getOrderById(orderId).setStatus("cancelled");
    }
}
