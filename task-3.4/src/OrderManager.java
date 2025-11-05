import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Order createOrder(int userId, List<Book> books) {
        Order order = new Order(userId);
        for (Book book : books) {
            if (!book.isAvailable()) {
                System.out.printf("%nКниги \'%s\' нет на складе. Создается запрос...", book.getName());
                requestManager.createRequest(book.getId());
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

    public void setOrderStatus(int orderId, String status) {
        getOrderById(orderId).setStatus(status);
    }
}
