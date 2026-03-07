package model.service;

import jakarta.transaction.Transactional;
import model.entity.Book;
import model.entity.Order;
import model.enums.OrderStatus;
import model.repository.OrderRepository;
import model.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private static final Logger logger = LogManager.getLogger();
    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final RequestService requestService;
    private final UserRepository userRepository;

    private static final String SORT_ERROR_MSG = "Невозможна сортировка по указанному полю. " +
            "Возможные значения параметра сортировки: completedAt, price, status";
    private static final String CHANGE_STATUS_ERROR_MSG = "Не удалось изменить статус заказа с id = {}: {}";
    private static final String ORDER_CREATION_ERROR_MSG = "Не удалось сформировать заказ: {}";
    private static final String ORDER_CREATION_SUCCESS_MSG = "Заказ успешно сформирован";
    private static final String ORDER_CANCELLATION_ERROR_MSG = "Не удалось отменить заказ с id = {}";
    private static final String USER_NOT_INITIALIZED_ERROR_MSG = "Ошибка инициализации пользователя";

    public OrderService(BookService bookService, RequestService requestService, OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
        this.requestService = requestService;
        this.userRepository = userRepository;
    }

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
            logger.error(SORT_ERROR_MSG);
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
            logger.error(CHANGE_STATUS_ERROR_MSG, orderId, e.getMessage());
        }
    }

    @Transactional
    public void createOrder(List<String> bookNames) {
        List<Book> books;
        try {
            books = bookService.formIdListFromNames(bookNames);
        } catch (NoSuchElementException e) {
            logger.error(ORDER_CREATION_ERROR_MSG, e.getMessage());
            return;
        }

        Order order;
        try {
            order = new Order(userRepository.getCurrentUserProfileReference());
        } catch (Exception e) {
            logger.error(USER_NOT_INITIALIZED_ERROR_MSG);
            return;
        }

        for (Book book : books) {
            if (!book.isAvailable()) {
                requestService.createRequestIfNotAvailable(book);
            }

            order.addBook(book);
        }

        orderRepository.save(order);
        logger.info(ORDER_CREATION_SUCCESS_MSG);
    }

    public void cancelOrder(int orderId) {
        try {
            orderRepository.setOrderStatus(orderId, OrderStatus.CANCELLED);
        } catch (IllegalStateException e) {
            logger.error(ORDER_CANCELLATION_ERROR_MSG, orderId);
        }
    }

    public void exportOrders(String filePath) {
        orderRepository.exportToCSV(filePath);
    }

    public void importOrders(String filePath) {
        orderRepository.importFromCSV(filePath);
    }
}
