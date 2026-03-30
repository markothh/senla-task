package controller;

import model.entity.Book;
import model.entity.Order;
import model.entity.User;
import model.enums.BookStatus;
import model.enums.UserRole;
import model.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StatisticsService statisticsService;

    private Order order;
    private Book book;

    @BeforeEach
    void setUp() {
        User user = new User(1, "testUser", "password", UserRole.USER);
        book = new Book(1, "Test Book", "Desc", "Author",
                "Genre", 500.0, BookStatus.IN_STOCK, 2020, LocalDate.now());
        order = new Order(1, user, List.of(book),
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(5),
                new model.status.CompletedOrderStatus());
        StatisticsController controller = new StatisticsController(statisticsService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getCompletedOrders_returnsFilteredOrders() throws Exception {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(statisticsService.getCompletedOrdersByPeriod(start, end))
                .thenReturn(List.of(order));

        mockMvc.perform(get("/statistics/orders")
                        .param("start_date", start.toString())
                        .param("end_date", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getCompletedOrders_noOrders_returnsEmpty() throws Exception {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(statisticsService.getCompletedOrdersByPeriod(start, end))
                .thenReturn(List.of());

        mockMvc.perform(get("/statistics/orders")
                        .param("start_date", start.toString())
                        .param("end_date", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getIncome_returnsTotalIncome() throws Exception {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(statisticsService.getIncomeByPeriod(start, end)).thenReturn(500.0);

        mockMvc.perform(get("/statistics/income")
                        .param("start_date", start.toString())
                        .param("end_date", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(500.0));
    }

    @Test
    void getIncome_noOrders_returnsZero() throws Exception {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now().plusDays(1);
        when(statisticsService.getIncomeByPeriod(start, end)).thenReturn(0.0);

        mockMvc.perform(get("/statistics/income")
                        .param("start_date", start.toString())
                        .param("end_date", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0.0));
    }

    @Test
    void getOverstockedBooks_returnsBooks() throws Exception {
        when(statisticsService.getOverstockedBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/statistics/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Book"));
    }

    @Test
    void getOverstockedBooks_noBooks_returnsEmpty() throws Exception {
        when(statisticsService.getOverstockedBooks()).thenReturn(List.of());

        mockMvc.perform(get("/statistics/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
