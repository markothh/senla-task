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

    private static final String GET_BY_ID_SUCCESS_MSG = "Заказ с id = {} получен";
    private static final String GET_BY_ID_ERROR_MSG = "Не удалось получить данные заказа с id = {}";
    private static final String GET_ALL_SUCCESS_MSG = "Список заказов успешно получен.";
    private static final String ADD_SUCCESS_MSG = "Заказ '{}' успешно добавлена";
    private static final String DELETE_BY_ID_SUCCESS_MSG = "Заказ с id = {} успешно удален";
    private static final String DELETE_BY_ID_ERROR_MSG = "Не удалось получить данные заказа с id = {}";
    private static final String IMPORT_SUCCESS_MSG = "Заказы успешно импортированы из файла '{}'";
    private static final String IMPORT_ERROR_MSG = "Не удалось импортировать заказ с id = {}: {}";
    private static final String SET_STATUS_ERROR_MSG = "Не удалось изменить статус заказа с id = {}: заказ не найден";
    private static final String SET_STATUS_SUCCESS_MSG = "Статус заказа с id = {} успешно изменен";

    public OrderRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Order> findById(int id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            logger.debug(GET_BY_ID_SUCCESS_MSG, id);
            return Optional.of(order);
        } else {
            logger.error(GET_BY_ID_ERROR_MSG, id);
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findAll() {
        TypedQuery<Order> query = em.createQuery(
                "select o from Order o " +
                        "join fetch o.user " +
                        "join fetch o.books", Order.class);
        logger.debug(GET_ALL_SUCCESS_MSG);
        return query.getResultList();
    }

    @Override
    public void save(Order obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info(ADD_SUCCESS_MSG);
    }

    @Override
    public void deleteById(int id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            em.remove(order);
        } else {
            logger.error(DELETE_BY_ID_ERROR_MSG, id);
        }
        logger.info(DELETE_BY_ID_SUCCESS_MSG, id);
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
                        logger.error(SET_STATUS_ERROR_MSG, id);
                    }
            );

            tx.commit();
            logger.info(SET_STATUS_SUCCESS_MSG, id);
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
                logger.error(IMPORT_ERROR_MSG, order.getId(), e.getMessage());
                tx.rollback();
            }
        }

        logger.info(IMPORT_SUCCESS_MSG, filePath);
    }
}
