package Model.Status;

import Model.Entity.Order;

import java.util.logging.Logger;

public class CompletedOrderStatus implements IOrderStatus {
    @Override
    public IOrderStatus resetToNew(Order order) {
        String errMessage = "Невозможно изменить статус на \"Новый\" у выполненного заказа.";
        Logger.getGlobal().severe(errMessage);
        throw new IllegalStateException(errMessage);
    }

    @Override
    public IOrderStatus complete(Order order) {
        return this;
    }

    @Override
    public IOrderStatus cancel(Order order) {
        String errMessage = "Нельзя отменить завершенный заказ.";
        Logger.getGlobal().severe(errMessage);
        throw new IllegalStateException(errMessage);
    }

    @Override
    public String toString() {
        return "Выполнен";
    }
}
