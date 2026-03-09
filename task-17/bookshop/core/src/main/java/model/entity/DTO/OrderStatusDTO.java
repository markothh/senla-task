package model.entity.DTO;

import jakarta.validation.constraints.NotNull;
import model.enums.OrderStatus;

public class OrderStatusDTO {
    @NotNull
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }
}
