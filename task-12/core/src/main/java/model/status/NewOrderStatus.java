package model.status;

import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

public class NewOrderStatus implements IOrderStatus {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public IOrderStatus resetToNew(Order order) {
        logger.info("Статус заказа с id = {} уже 'Новый'", order.getId());
        return this;
    }

    @Override
    public IOrderStatus complete(Order order) {
        if (order.areBooksAvailable()) {
            order.setCompletedAt(LocalDate.now());
            return new CompletedOrderStatus();
        } else {
            logger.error("Невозможно изменить статус заказа с id = {} на 'Выполнен': " +
                    "не все книги доступны для заказа.", order.getId());
            throw new IllegalStateException();
        }
    }

    @Override
    public IOrderStatus cancel(Order order) {
        logger.info("Статус заказа с id = {} изменен со статуса 'Новый' на 'Отменен'", order.getId());
        return new CancelledOrderStatus();
    }

    @Override
    public String toString() {
        return "NEW";
    }
}
