package Model.Service;

import Model.Entity.Book;
import Model.Entity.Order;
import Model.Entity.User;
import Model.Enum.OrderStatus;
import Model.Repository.OrderRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class OrderService {
    private final OrderRepository orderRepository = OrderRepository.getInstance();
    private final RequestService requestService;

    public OrderService(RequestService requestService) {
        this.requestService = requestService;
    }

    public List<Order> getOrders() {
        return orderRepository.getOrders();
    }

    public List<Order> getSortedOrders(String sortBy, boolean isReversed) {
        HashMap<String, Comparator<Order>> comparators = new HashMap<>() {{
            put("completedAt", Comparator.comparing(Order::getCompletedAt, Comparator.nullsLast(LocalDate::compareTo)));
            put("price", Comparator.comparing(Order::getSum));
            put("status", Comparator.comparing(Order::getStatus));
        }};

        Comparator<Order> comparator = comparators.get(sortBy);
        if (comparator == null) {
            String errMessage = "Невозможна сортировка по указанному полю. " +
                    "Возможные значения параметра сортировки: completedAt, price, status";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return orderRepository.getOrders().stream()
                .sorted(comparator)
                .toList();
    }

    public String getOrderInfo(int orderId) {
        return orderRepository.getOrderInfo(orderId);
    }

    public void setOrderStatus(int orderId, OrderStatus status) {
        orderRepository.setOrderStatus(orderId, status);
    }

    public Order createOrder(User user, List<Book> books) {
        Order order = new Order(user);
        for (Book book : books) {
            if (!book.isAvailable()) {
                System.out.printf("%nКниги '%s' нет на складе. Создается запрос...", book.getName());
                requestService.createRequest(book);
            }

            order.addBook(book);
        }

        orderRepository.save(order);
        System.out.printf("%nСоздан заказ: %n%s", order);

        return order;
    }

    public void cancelOrder(int orderId) {
        orderRepository.setOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    public void exportOrders(String filePath) {
        orderRepository.exportOrders(filePath);
    }

    public void importOrders(String filePath) {
        orderRepository.importOrders(filePath);
    }
}
