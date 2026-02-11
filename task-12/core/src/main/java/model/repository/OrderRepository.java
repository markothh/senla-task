package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import model.entity.Order;
import model.enums.OrderStatus;
import model.service.CSVHandler.OrderCSVHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository implements IRepository<Order> {
    private static final Logger logger = LogManager.getLogger();

    @Lazy
    private final OrderCSVHandler csvHandler;

    @PersistenceContext
    private EntityManager em;

    private static final String GET_BY_ID_SUCCESS_MSG = "Заказ с id = {} получен";
    private static final String GET_BY_ID_ERROR_MSG = "Не удалось получить данные заказа с id = {}";
    private static final String GET_ALL_SUCCESS_MSG = "Список заказов успешно получен.";
    private static final String ADD_SUCCESS_MSG = "Заказ '{}' успешно добавлена";
    private static final String DELETE_BY_ID_SUCCESS_MSG = "Заказ с id = {} успешно удален";
    private static final String DELETE_BY_ID_ERROR_MSG = "Не удалось получить данные заказа с id = {}";
    private static final String IMPORT_SUCCESS_MSG = "Заказы успешно импортированы из файла '{}'";
    private static final String SET_STATUS_ERROR_MSG = "Не удалось изменить статус заказа с id = {}: заказ не найден";

    public OrderRepository(OrderCSVHandler csvHandler) {
        this.csvHandler = csvHandler;
    }

    @Override
    public Optional<Order> findById(int id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            logger.info(GET_BY_ID_SUCCESS_MSG, id);
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
        logger.info(GET_ALL_SUCCESS_MSG);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(Order obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info(ADD_SUCCESS_MSG, obj.getId());
    }

    @Override
    @Transactional
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

    @Transactional
    public void setOrderStatus (int id, OrderStatus status) throws IllegalStateException {
        findById(id).ifPresentOrElse(
                order -> order.setStatus(status),
                () -> {
                    logger.error(SET_STATUS_ERROR_MSG, id);
                }
        );
    }

    public void exportToCSV(String filePath) {
        csvHandler.exportToCSV(findAll(), filePath);
    }

    @Transactional
    public void importFromCSV(String filePath) {
        for (Order order : csvHandler.importFromCSV(filePath)) {
            em.createNativeQuery("insert into orders (" +
                    "user_id, " +
                    "created_at, " +
                    "completed_at, " +
                    "status)" +
                    "values (:user_id, :created_at, :completed_at, :status)")
                    .setParameter("user_id", order.getUser().getId())
                    .setParameter("created_at", order.getCreatedAt())
                    .setParameter("completed_at", order.getCompletedAt())
                    .setParameter("status", order.getStatus().toString())
                    .executeUpdate();
        }

        logger.info(IMPORT_SUCCESS_MSG, filePath);
    }
}
