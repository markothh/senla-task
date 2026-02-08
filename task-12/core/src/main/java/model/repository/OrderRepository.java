package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.config.JPAConfig;
import model.entity.Order;
import model.enums.OrderStatus;
import model.service.CSVHandler.CSVHandlers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class OrderRepository implements IRepository<Order> {
    private static final Logger logger = LogManager.getLogger();
    private final EntityManager em;

    public OrderRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Order> findById(int id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            logger.info("Заказ с id = {} получен", id);
            return Optional.of(order);
        } else {
            logger.error("Не удалось получить данные заказа с id = {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findAll() {
        TypedQuery<Order> query = em.createQuery(
                "select o from Order o " +
                        "join fetch o.user " +
                        "join fetch o.books", Order.class);
        logger.info("Список заказов успешно получен.");
        return query.getResultList();
    }

    @Override
    public void save(Order obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info("Заказ успешно добавлен");
    }

    @Override
    public void deleteById(int id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            em.remove(order);
        } else {
            logger.error("Не удалось получить данные заказа с id = {}", id);
        }
        logger.info("Заказ с id = {} успешно удален", id);
    }

    public String getOrderInfo(int orderId) {
        return findById(orderId)
                .map(Order::getInfo)
                .orElse("");
    }

    public void setOrderStatus (int id, OrderStatus status) throws IllegalStateException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            findById(id).ifPresentOrElse(
                    order -> order.setStatus(status),
                    () -> {
                        logger.error("Не удалось изменить статус заказа с id = {}: заказ не найден", id);
                    }
            );

            tx.commit();
            logger.info("Статус заказа с id = {} успешно изменена", id);
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.orders().exportToCSV(filePath);
    }

    public void importFromCSV(String filePath) {
        for (Order order : CSVHandlers.orders().importFromCSV(filePath)) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                save(order);
                tx.commit();
            } catch (Exception e) {
                logger.error("Не удалось импортировать заказ с id = {}: {}", order.getId(), e.getMessage());
                tx.rollback();
            }
        }

        logger.info("Заказы успешно импортированы из файла '{}'", filePath);
    }
}
