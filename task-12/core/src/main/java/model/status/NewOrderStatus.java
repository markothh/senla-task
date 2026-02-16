package model.status;

import model.entity.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

public class NewOrderStatus implements IOrderStatus {
    private static final Logger logger = LogManager.getLogger();

    private static final String NEW_MSG = "Статус заказа с id = {} уже 'Новый'";
    private static final String CANCEL_MSG = "Статус заказа с id = {} изменен со статуса 'Новый' на 'Отменен'";
    private static final String COMPLETE_SUCCESS_MSG = "Статус заказа с id = {} уже 'Новый'";
    private static final String COMPLETE_ERROR_MSG = "Невозможно изменить статус заказа с id = {} на 'Выполнен': " +
            "не все книги доступны для заказа.";

    @Override
    public IOrderStatus resetToNew(Order order) {
        logger.info(NEW_MSG, order.getId());
        return this;
    }

    @Override
    public IOrderStatus complete(Order order) {
        if (order.areBooksAvailable()) {
            order.setCompletedAt(LocalDate.now());
            logger.info(COMPLETE_SUCCESS_MSG, order.getId());
            return new CompletedOrderStatus();
        } else {
            logger.error(COMPLETE_ERROR_MSG, order.getId());
            throw new IllegalStateException();
        }
    }

    @Override
    public IOrderStatus cancel(Order order) {
        logger.info(CANCEL_MSG, order.getId());
        return new CancelledOrderStatus();
    }

    @Override
    public String toString() {
        return "NEW";
    }
}
