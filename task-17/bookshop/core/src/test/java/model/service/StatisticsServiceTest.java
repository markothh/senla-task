package model.service;

import model.entity.Book;
import model.entity.Order;
import model.entity.User;
import model.enums.BookStatus;
import model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private BookService bookService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private StatisticsService statisticsService;

    private User user;
    private Book book;
    private Order completedOrder;

    @BeforeEach
    void setUp() throws Exception {
        user = new User(1, "testUser", "password", UserRole.USER);
        book = new Book(1, "Test Book", "Desc", "Author",
                "Genre", 500.0, BookStatus.IN_STOCK, 2020, LocalDate.now());
        completedOrder = new Order(1, user, List.of(book),
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(5),
                new model.status.CompletedOrderStatus());
        Field field = StatisticsService.class.getDeclaredField("staleMonths");
        field.setAccessible(true);
        field.set(statisticsService, 6);
    }

    @Test
    void getCompletedOrdersByPeriod_returnsFilteredOrders() {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(orderService.getOrders()).thenReturn(List.of(completedOrder));

        List<Order> result = statisticsService.getCompletedOrdersByPeriod(start, end);

        assertEquals(1, result.size());
    }

    @Test
    void getCompletedOrdersByPeriod_noCompletedOrders_returnsEmpty() {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(orderService.getOrders()).thenReturn(new ArrayList<>());

        List<Order> result = statisticsService.getCompletedOrdersByPeriod(start, end);

        assertTrue(result.isEmpty());
    }

    @Test
    void getIncomeByPeriod_returnsTotalIncome() {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(orderService.getOrders()).thenReturn(List.of(completedOrder));

        double result = statisticsService.getIncomeByPeriod(start, end);

        assertEquals(500.0, result, 0.01);
    }

    @Test
    void getIncomeByPeriod_noOrders_returnsZero() {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(orderService.getOrders()).thenReturn(new ArrayList<>());

        double result = statisticsService.getIncomeByPeriod(start, end);

        assertEquals(0.0, result, 0.01);
    }

    @Test
    void getOverstockedBooks_returnsStaleBooks() {
        Book staleBook = new Book(2, "Stale Book", "Desc", "Author",
                "Genre", 300.0, BookStatus.IN_STOCK, 2020,
                LocalDate.now().minusMonths(12));
        when(bookService.getBooks()).thenReturn(List.of(book, staleBook));

        List<Book> result = statisticsService.getOverstockedBooks();

        assertEquals(1, result.size());
        assertEquals("Stale Book", result.get(0).getName());
    }

    @Test
    void getOverstockedBooks_noStaleBooks_returnsEmpty() {
        when(bookService.getBooks()).thenReturn(List.of(book));

        List<Book> result = statisticsService.getOverstockedBooks();

        assertTrue(result.isEmpty());
    }
}
