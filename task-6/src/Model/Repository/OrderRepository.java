package Model.Repository;

import Model.Entity.Order;
import Model.Enum.OrderStatus;
import Model.Service.CSVHandler.CSVHandlers;

import java.util.*;

public class OrderRepository {
    private static OrderRepository INSTANCE;
    private List<Order> orders = new ArrayList<>();

    private OrderRepository() {}

    public static OrderRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderRepository();
        }
        return INSTANCE;
    }

    public void save(Order order) {
        orders.add(order);
    }

    public List<Order> getOrders() {
        return orders;
    }

    private Order getOrderById(int orderId) {
        Optional<Order> order = orders.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst();

        return order.orElse(null);
    }

    public String getOrderInfo(int orderId) {
        return getOrderById(orderId).getInfo();
    }

    public void setOrderStatus(int orderId, OrderStatus status) {
        getOrderById(orderId).setStatus(status);
    }

    public void exportOrders(String filePath) {
        CSVHandlers.orders().exportToCSV(filePath);
    }

    public void importOrders(String filePath) {
        Map<Integer, Order> merged = new HashMap<>();
        for (Order order : orders) {
            merged.put(order.getId(), order);
        }

        for (Order order : CSVHandlers.orders().importFromCSV(filePath)) {
            merged.put(order.getId(), order);
        }
        orders = new ArrayList<>(merged.values());
    }
}
