package model.status;

import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CompletedOrderStatus implements IOrderStatus {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public IOrderStatus resetToNew(Order order) {
        logger.error("Статус заказа с id = {} 'Выполнен'. Невозможно изменить статус на 'Новый' у выполненного заказа.", order.getId());
        throw new IllegalStateException();
    }

    @Override
    public IOrderStatus complete(Order order) {
        logger.info("Статус заказа с id = {} уже 'Выполнен'", order.getId());
        return this;
    }

    @Override
    public IOrderStatus cancel(Order order) {
        logger.error("Статус заказа с id = {} 'Выполнен'. Невозможно изменить статус на 'Отменен' у выполненного заказа.", order.getId());
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return "COMPLETED";
    }
}
