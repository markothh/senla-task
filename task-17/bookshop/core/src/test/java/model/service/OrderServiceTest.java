package model.service;

import model.entity.Book;
import model.entity.Order;
import model.entity.User;
import model.enums.BookStatus;
import model.enums.OrderSortField;
import model.enums.OrderStatus;
import model.enums.UserRole;
import model.repository.OrderRepository;
import model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookService bookService;

    @Mock
    private RequestService requestService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User(1, "testUser", "password", UserRole.USER);
        book = new Book(1, "Test Book", "Desc", "Author",
                "Genre", 500.0, BookStatus.IN_STOCK, 2020, LocalDate.now());
        order = new Order(1, user, List.of(book), LocalDate.now(), null,
                new model.status.NewOrderStatus());
    }

    @Test
    void getOrders_returnsListOfOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.getOrders();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void getOrders_returnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        List<Order> result = orderService.getOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    void getSortedOrders_sortedByPrice_returnsSortedList() {
        Book cheapBook = new Book(2, "Cheap", "Desc", "Author",
                "Genre", 200.0, BookStatus.IN_STOCK, 2021, LocalDate.now());
        Order expensiveOrder = new Order(2, user, List.of(book), LocalDate.now(), null,
                new model.status.NewOrderStatus());
        Order cheapOrder = new Order(3, user, List.of(cheapBook), LocalDate.now(), null,
                new model.status.NewOrderStatus());
        when(orderRepository.findAll()).thenReturn(List.of(expensiveOrder, cheapOrder));

        List<Order> result = orderService.getSortedOrders(OrderSortField.PRICE, false);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getSum() <= result.get(1).getSum());
    }

    @Test
    void getSortedOrders_invalidSortField_throwsNoSuchElement() {
        assertThrows(NoSuchElementException.class,
                () -> orderService.getSortedOrders(null, false));
    }

    @Test
    void getOrderById_found_returnsOrder() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getOrderById_notFound_throwsNoSuchElement() {
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> orderService.getOrderById(99));
    }

    @Test
    void setOrderStatus_returnsUpdatedOrder() {
        when(orderRepository.setOrderStatus(1, OrderStatus.COMPLETED)).thenReturn(order);

        Order result = orderService.setOrderStatus(1, OrderStatus.COMPLETED);

        assertNotNull(result);
        verify(orderRepository).setOrderStatus(1, OrderStatus.COMPLETED);
    }

    @Test
    void createOrder_success_returnsCreatedOrder() {
        List<Integer> bookIds = List.of(1);
        when(bookService.formBookListFromIds(bookIds)).thenReturn(List.of(book));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Order result = orderService.createOrder(bookIds, 1);

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_booksNotFound_throwsNoSuchElement() {
        when(bookService.formBookListFromIds(List.of(99)))
                .thenThrow(new NoSuchElementException("Книги не найдены"));

        assertThrows(NoSuchElementException.class,
                () -> orderService.createOrder(List.of(99), 1));
    }

    @Test
    void createOrder_userNotFound_throwsNoSuchElement() {
        when(bookService.formBookListFromIds(List.of(1))).thenReturn(List.of(book));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> orderService.createOrder(List.of(1), 99));
    }

    @Test
    void cancelOrder_returnsCancelledOrder() {
        when(orderRepository.setOrderStatus(1, OrderStatus.CANCELLED)).thenReturn(order);

        Order result = orderService.cancelOrder(1);

        assertNotNull(result);
        verify(orderRepository).setOrderStatus(1, OrderStatus.CANCELLED);
    }
}
