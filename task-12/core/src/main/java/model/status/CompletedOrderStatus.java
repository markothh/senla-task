package model.status;

import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CompletedOrderStatus implements IOrderStatus {
    private static final Logger logger = LogManager.getLogger();

    private static final String NEW_MSG = "Статус заказа с id = {} 'Выполнен'. Невозможно изменить статус на 'Новый' у выполненного заказа.";
    private static final String CANCEL_MSG = "Статус заказа с id = {} уже 'Выполнен'";
    private static final String COMPLETE_MSG = "Статус заказа с id = {} 'Выполнен'. Невозможно изменить статус на 'Отменен' у выполненного заказа.";

    @Override
    public IOrderStatus resetToNew(Order order) {
        logger.error(NEW_MSG, order.getId());
        throw new IllegalStateException();
    }

    @Override
    public IOrderStatus complete(Order order) {
        logger.info(CANCEL_MSG, order.getId());
        return this;
    }

    @Override
    public IOrderStatus cancel(Order order) {
        logger.error(COMPLETE_MSG, order.getId());
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return "COMPLETED";
    }
}
