package Model.Status;

import Model.Entity.Order;
import Model.Enum.OrderStatus;

import java.io.Serializable;

public interface IOrderStatus extends Serializable {
    public IOrderStatus resetToNew(Order order);
    public IOrderStatus complete(Order order);
    public IOrderStatus cancel(Order order);

    static IOrderStatus from(OrderStatus status) {
        return switch (status) {
            case NEW -> new NewOrderStatus();
            case CANCELLED -> new CancelledOrderStatus();
            case COMPLETED -> new CompletedOrderStatus();
        };
    }
}
