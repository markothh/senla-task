package Model.Status;

import Model.Entity.Order;

import java.util.logging.Logger;

public class CancelledOrderStatus implements IOrderStatus {
    @Override
    public IOrderStatus resetToNew(Order order) {
        return new NewOrderStatus();
    }

    @Override
    public IOrderStatus complete(Order order) {
        String errMessage = "Невозможно выполнить отмененный заказ.";
        Logger.getGlobal().severe(errMessage);
        throw new IllegalStateException(errMessage);
    }

    @Override
    public IOrderStatus cancel(Order order) {
        return this;
    }

    @Override
    public String toString() {
        return "Отменен";
    }
}
