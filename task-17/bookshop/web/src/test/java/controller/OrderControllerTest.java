package controller;

import model.entity.Book;
import model.entity.DTO.OrderCreationDTO;
import model.entity.DTO.UserPrincipal;
import model.entity.Order;
import model.entity.User;
import model.enums.BookStatus;
import model.enums.OrderSortField;
import model.enums.UserRole;
import model.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private Authentication authentication;

    private OrderController orderController;
    private Order order;
    private Order otherOrder;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        User user = new User(1, "testUser", "password", UserRole.USER);
        User otherUser = new User(2, "otherUser", "password", UserRole.USER);
        userPrincipal = new UserPrincipal(1, "testUser");
        Book book = new Book(1, "Test Book", "Desc", "Author",
                "Genre", 500.0, BookStatus.IN_STOCK, 2020, LocalDate.now());
        order = new Order(1, user, List.of(book), LocalDate.now(), null,
                new model.status.NewOrderStatus());
        otherOrder = new Order(1, otherUser, List.of(), LocalDate.now(), null,
                new model.status.NewOrderStatus());
        orderController = new OrderController(orderService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();
    }

    @Test
    void getOrders_noSort_returnsAllOrders() throws Exception {
        when(orderService.getOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOrders_emptyList_returnsEmpty() throws Exception {
        when(orderService.getOrders()).thenReturn(List.of());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getOrders_withSort_returnsSortedOrders() throws Exception {
        when(orderService.getSortedOrders(OrderSortField.PRICE, false))
                .thenReturn(List.of(order));

        mockMvc.perform(get("/orders")
                        .param("sort", "PRICE")
                        .param("isReversed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOrderById_sameUser_returnsOrder() {
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(orderService.getOrderById(1)).thenReturn(order);

        ResponseEntity<?> result = orderController.getOrderById(1, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getOrderById_notFound_throwsNoSuchElement() {
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(orderService.getOrderById(99))
                .thenThrow(new NoSuchElementException("Заказ не найден"));

        assertThrows(NoSuchElementException.class,
                () -> orderController.getOrderById(99, authentication));
    }

    @Test
    void getOrderById_differentUser_returnsForbidden() {
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(orderService.getOrderById(1)).thenReturn(otherOrder);

        ResponseEntity<?> result = orderController.getOrderById(1, authentication);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    void cancelOrder_sameUser_returnsCancelledOrder() {
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(orderService.getOrderById(1)).thenReturn(order);
        when(orderService.cancelOrder(1)).thenReturn(order);

        ResponseEntity<?> result = orderController.changeStatus(1, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void cancelOrder_differentUser_returnsForbidden() {
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(orderService.getOrderById(1)).thenReturn(otherOrder);

        ResponseEntity<?> result = orderController.changeStatus(1, authentication);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    void createOrder_success_returnsCreatedOrder() {
        OrderCreationDTO orderDTO = new OrderCreationDTO();
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(orderService.createOrder(any(), anyInt())).thenReturn(order);

        ResponseEntity<?> result = orderController.createOrder(orderDTO, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }
}
