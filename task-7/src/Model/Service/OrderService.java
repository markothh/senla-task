package Model.Service;

import Model.Annotations.Inject;
import Model.Config.DBConnection;
import Model.Entity.Book;
import Model.Entity.DTO.UserProfile;
import Model.Entity.Order;
import Model.Enum.OrderStatus;
import Model.Repository.OrderRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class OrderService {
    private static OrderService INSTANCE;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private RequestService requestService;

    public List<Order> getOrders() {
        return orderRepository.findAll();
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

        return orderRepository.findAll().stream()
                .sorted(comparator)
                .toList();
    }

    public String getOrderInfo(int orderId) {
        return orderRepository.getOrderInfo(orderId);
    }

    public void setOrderStatus(int orderId, OrderStatus status) {
        orderRepository.setOrderStatus(orderId, status);
    }

    public void createOrder(UserProfile user, List<Book> books) {
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            Order order = new Order(user);
            for (Book book : books) {
                if (!book.isAvailable()) {
                    requestService.createRequest(book);
                }

                order.addBook(book);
            }

            orderRepository.save(order);
            System.out.printf("%nСоздан заказ: %n%s", order);
        }
        catch (SQLException e) {
            try {
                connection.rollback();
            }
            catch (SQLException e1){
                throw new RuntimeException(e1);
            }

            String errMessage = String.format("Не удалось создать заказ: %s", e.getMessage());
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(e);
        }
        finally {
            try {
                connection.setAutoCommit(true);
            }
            catch (SQLException e1){
                throw new RuntimeException(e1);
            }
        }
    }

    public void cancelOrder(int orderId) {
        orderRepository.setOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    public void exportOrders(String filePath) {
        orderRepository.exportToCSV(filePath);
    }

    public void importOrders(String filePath) {
        orderRepository.importFromCSV(filePath);
    }

    public static OrderService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderService();
        }
        return INSTANCE;
    }

    private OrderService() {}
}
