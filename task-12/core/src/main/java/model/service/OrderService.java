package model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.annotations.Inject;
import model.config.JPAConfig;
import model.entity.Book;
import model.entity.DTO.UserProfile;
import model.entity.Order;
import model.entity.User;
import model.enums.OrderStatus;
import model.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public final class OrderService {
    private static final Logger logger = LogManager.getLogger();
    private static OrderService INSTANCE;
    private final OrderRepository orderRepository = new OrderRepository(JPAConfig.getEntityManager());

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
            logger.error("Невозможна сортировка по указанному полю. " +
                    "Возможные значения параметра сортировки: completedAt, price, status");
            return List.of();
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
        try {
            orderRepository.setOrderStatus(orderId, status);
        } catch (IllegalStateException e) {
            logger.error("Не удалось изменить статус заказа с id = {}: {}", orderId, e.getMessage());
        }
    }

    public void createOrder(UserProfile user, List<String> bookNames) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            BookService bookService = new BookService(em);
            RequestService requestService = new RequestService(em);
            List<Book> books;
            try {
                books = bookService.formIdListFromNames(bookNames);
            } catch (NoSuchElementException e) {
                logger.error("Не удалось сформировать заказ: {}", e.getMessage());
                return;
            }

            EntityTransaction tx = em.getTransaction();

            try {
                Order order = new Order(em.getReference(User.class, user.getId()));
                for (Book book : books) {
                    if (!book.isAvailable()) {
                        requestService.createRequestIfNotAvailable(em, book);
                    }

                    order.addBook(book);
                }

                orderRepository.save(order);
            } catch (Exception e) {
                logger.info("Заказ не был создан. Изменения, касающиеся этого заказа, не были применены");
            }
        }
    }

    public void cancelOrder(int orderId) {
        try {
            orderRepository.setOrderStatus(orderId, OrderStatus.CANCELLED);
        } catch (IllegalStateException e) {
            logger.error("Не удалось отменить заказ с id = {}", orderId);
        }
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

    private OrderService() { }
}
