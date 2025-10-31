import java.time.LocalDateTime;
import java.util.Date;

public class NewOrderStatus implements IOrderStatus {
    @Override
    public IOrderStatus resetToNew(Order order) {
        return this;
    }

    @Override
    public IOrderStatus complete(Order order) {
        if (order.areBooksAvailable()) {
            order.setCompletedAt(LocalDateTime.now());
            return new CompleteOrderStatus();
        }
        else {
            throw new IllegalStateException("Нельзя выполнить заказ, если не все книги доступны");
        }
    }

    @Override
    public IOrderStatus cancel(Order order) {
        return new CancelledOrderStatus();
    }

    @Override
    public String toString() {
        return "Новый";
    }
}
