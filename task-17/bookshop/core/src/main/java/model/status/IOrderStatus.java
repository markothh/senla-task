package model.status;

import model.entity.Order;
import model.enums.OrderStatus;

import java.io.Serializable;

public interface IOrderStatus extends Serializable {
    IOrderStatus resetToNew(Order order);
    IOrderStatus complete(Order order);
    IOrderStatus cancel(Order order);

    static IOrderStatus from(OrderStatus status) {
        return switch (status) {
            case NEW -> new NewOrderStatus();
            case CANCELLED -> new CancelledOrderStatus();
            case COMPLETED -> new CompletedOrderStatus();
        };
    }
}
