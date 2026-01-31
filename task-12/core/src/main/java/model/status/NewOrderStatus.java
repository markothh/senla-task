package model.status;

import model.entity.Order;

import java.time.LocalDate;
import java.util.logging.Logger;

public class NewOrderStatus implements IOrderStatus {
    @Override
    public IOrderStatus resetToNew(Order order) {
        return this;
    }

    @Override
    public IOrderStatus complete(Order order) {
        if (order.areBooksAvailable()) {
            order.setCompletedAt(LocalDate.now());
            return new CompletedOrderStatus();
        } else {
            String errMessage = "Нельзя выполнить заказ, если не все книги доступны";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalStateException(errMessage);
        }
    }

    @Override
    public IOrderStatus cancel(Order order) {
        return new CancelledOrderStatus();
    }

    @Override
    public String toString() {
        return "NEW";
    }
}
