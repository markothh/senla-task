package model.service;

import jakarta.transaction.Transactional;
import model.entity.Book;
import model.entity.Order;
import model.enums.OrderSortField;
import model.enums.OrderStatus;
import model.repository.OrderRepository;
import model.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
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
    private static final String ORDER_CREATION_SUCCESS_MSG = "Заказ успешно сформирован";
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

    public List<Order> getSortedOrders(OrderSortField sortBy, boolean isReversed) {
        HashMap<OrderSortField, Comparator<Order>> comparators = new HashMap<>() {{
            put(OrderSortField.COMPLETED_AT, Comparator.comparing(Order::getCompletedAt, Comparator.nullsLast(LocalDate::compareTo)));
            put(OrderSortField.PRICE, Comparator.comparing(Order::getSum));
            put(OrderSortField.STATUS, Comparator.comparing(Order::getStatus));
        }};

        Comparator<Order> comparator = comparators.get(sortBy);
        if (comparator == null) {
            logger.error(SORT_ERROR_MSG);
            throw new NoSuchElementException(SORT_ERROR_MSG);
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return orderRepository.findAll().stream()
                .sorted(comparator)
                .toList();
    }

    public Order setOrderStatus(int orderId, OrderStatus status) {
        return orderRepository.setOrderStatus(orderId, status);
    }

    @Transactional
    public Order createOrder(List<Integer> bookIds, int userId) {
        List<Book> books = bookService.formBookListFromIds(bookIds);

        Order order = new Order(userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_INITIALIZED_ERROR_MSG)));

        for (Book book : books) {
            if (!book.isAvailable()) {
                requestService.createRequestIfNotAvailable(book);
            }

            order.addBook(book);
        }

        orderRepository.save(order);
        logger.info(ORDER_CREATION_SUCCESS_MSG);
        return order;
    }

    public Order cancelOrder(int orderId) {
        return orderRepository.setOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    public void exportOrders(OutputStream os) {
        orderRepository.exportToCSV(os);
    }

    public void importOrders(File file) {
        orderRepository.importFromCSV(file);
    }
}
