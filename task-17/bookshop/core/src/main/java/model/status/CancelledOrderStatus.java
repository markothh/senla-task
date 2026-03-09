package model.status;

import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CancelledOrderStatus implements IOrderStatus {
    private static final Logger logger = LogManager.getLogger();

    private static final String NEW_MSG = "Статус заказа с id = {} изменен со статуса 'Отменен' на 'Новый'";
    private static final String CANCEL_MSG = "Статус заказа с id = {} уже 'Отменен'";
    private static final String COMPLETE_MSG = "Статус заказа с id = {} 'Отменен'. Невозможно выполнить отмененный заказ.";

    @Override
    public IOrderStatus resetToNew(Order order) {
        logger.info(NEW_MSG, order.getId());
        return new NewOrderStatus();
    }

    @Override
    public IOrderStatus complete(Order order) {
        logger.error(COMPLETE_MSG, order.getId());
        throw new IllegalStateException();
    }

    @Override
    public IOrderStatus cancel(Order order) {
        logger.info(CANCEL_MSG, order.getId());
        return this;
    }

    @Override
    public String toString() {
        return "CANCELLED";
    }
}
