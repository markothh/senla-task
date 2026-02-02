package model.status;

import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CancelledOrderStatus implements IOrderStatus {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public IOrderStatus resetToNew(Order order) {
        logger.info("Статус заказа с id = {} изменен со статуса 'Отменен' на 'Новый'", order.getId());
        return new NewOrderStatus();
    }

    @Override
    public IOrderStatus complete(Order order) {
        logger.error("Статус заказа с id = {} 'Отменен'. Невозможно выполнить отмененный заказ.", order.getId());
        throw new IllegalStateException();
    }

    @Override
    public IOrderStatus cancel(Order order) {
        logger.info("Статус заказа с id = {} уже 'Отменен'", order.getId());
        return this;
    }

    @Override
    public String toString() {
        return "CANCELLED";
    }
}
