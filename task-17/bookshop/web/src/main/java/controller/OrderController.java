package controller;

import model.entity.DTO.OrderCreationDTO;
import model.entity.DTO.OrderDTO;
import model.entity.DTO.OrderStatusDTO;
import model.enums.OrderSortField;
import model.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDTO> getOrders(
            @RequestParam(value = "sort", required = false) OrderSortField field,
            @RequestParam(value = "isReversed", required = false) boolean isReversed
    ) {
        if (field != null) {
            return orderService.getSortedOrders(field, isReversed).stream()
                    .map(OrderDTO::new)
                    .toList();
        } else {
            return orderService.getOrders().stream()
                    .map(OrderDTO::new)
                    .toList();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> changeStatus(
                @PathVariable("id") int id,
                @RequestBody OrderStatusDTO statusDTO) {
        return ResponseEntity.ok(new OrderDTO(orderService.setOrderStatus(id, statusDTO.getStatus())));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> changeStatus(
                @PathVariable("id") int id) {
        return ResponseEntity.ok(new OrderDTO(orderService.cancelOrder(id)));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody OrderCreationDTO orderDTO
    ) {
        return new ResponseEntity<>(
                new OrderDTO(orderService.createOrder(orderDTO.getBooks(), orderDTO.getUserId())),
                HttpStatus.CREATED
        );
    }
}
