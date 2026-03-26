package controller;

import model.entity.DTO.MessageDTO;
import model.entity.DTO.OrderCreationDTO;
import model.entity.DTO.OrderDetailsDTO;
import model.entity.DTO.OrderStatusDTO;
import model.entity.DTO.UserPrincipal;
import model.entity.Order;
import model.enums.OrderSortField;
import model.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = LogManager.getLogger();
    private String ACCESS_DENIED_ERROR_MSG = "Вы не имеете доступ к заказам других пользователей";
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDetailsDTO> getOrders(
            @RequestParam(value = "sort", required = false) OrderSortField field,
            @RequestParam(value = "isReversed", required = false) boolean isReversed
    ) {
        if (field != null) {
            return orderService.getSortedOrders(field, isReversed).stream()
                    .map(OrderDetailsDTO::new)
                    .toList();
        } else {
            return orderService.getOrders().stream()
                    .map(OrderDetailsDTO::new)
                    .toList();
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getOrderById(@PathVariable("id") int id, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        int currentUserId = user.getId();

        Order order = orderService.getOrderById(id);

        if (!(order.getUser().getId() == currentUserId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new MessageDTO("FORBIDDEN", ACCESS_DENIED_ERROR_MSG));
        }

        return ResponseEntity.ok(new OrderDetailsDTO(order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDetailsDTO> changeStatus(
                @PathVariable("id") int id,
                @RequestBody OrderStatusDTO statusDTO) {
        return ResponseEntity.ok(new OrderDetailsDTO(orderService.setOrderStatus(id, statusDTO.getStatus())));
    }

    @PatchMapping("/{id}/cancel")
    @Transactional
    public ResponseEntity<?> changeStatus(
                @PathVariable("id") int id,
                Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        int currentUserId = user.getId();

        Order order = orderService.getOrderById(id);

        if (!(order.getUser().getId() == currentUserId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ACCESS_DENIED_ERROR_MSG);
        }

        return ResponseEntity.ok(new OrderDetailsDTO(orderService.cancelOrder(id)));
    }

    @PostMapping
    public ResponseEntity<OrderDetailsDTO> createOrder(
            @RequestBody OrderCreationDTO orderDTO,
            Authentication authentication
    ) {
        return new ResponseEntity<>(
                new OrderDetailsDTO(orderService.createOrder(orderDTO.getBooks(),
                        ((UserPrincipal) authentication.getPrincipal()).getId())),
                HttpStatus.CREATED
        );
    }
}
