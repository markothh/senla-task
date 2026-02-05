package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.config.JPAConfig;
import model.entity.Order;
import model.entity.Request;
import model.service.CSVHandler.CSVHandlers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class RequestRepository implements IRepository<Request> {
    private static final Logger logger = LogManager.getLogger();
    private static RequestRepository INSTANCE;

    public static RequestRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestRepository();
        }
        return INSTANCE;
    }

    @Override
    public Optional<Request> findById(int id) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            Request request = em.find(Request.class, id);
            if (request != null) {
                logger.info("Запрос с id = {} получен", id);
                return Optional.of(request);
            } else {
                logger.error("Не удалось получить данные запроса с id = {}", id);
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Request> findAll() {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            TypedQuery<Request> query = em.createQuery(
                    "select r from Request r " +
                       "join fetch r.book", Request.class);
            logger.info("Список запросов успешно получен.");
            return query.getResultList();
        }
    }

    @Override
    public void save(EntityManager em, Request obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info("Запрос успешно добавлен");
    }

    @Override
    public void deleteById(EntityManager em, int id) {
        Request request = em.find(Request.class, id);
        if (request != null) {
            em.remove(request);
        } else {
            logger.error("Не удалось получить данные запроса с id = {}", id);
        }
        logger.info("Запрос с id = {} успешно удален", id);
    }

    public Optional<Request> findByBookId(int bookId) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            TypedQuery<Request> query = em.createQuery("select r from Request r where r.book.id = :bookId", Request.class);
            query.setParameter("bookId", bookId);
            try {
                Request request = query.getSingleResult();

                logger.info("Успешно получены запросы на книгу с id = {}", bookId);
                return Optional.of(request);
            } catch (Exception e) {
                logger.error("Запросы на книгу с id = {} не найдены", bookId);
                return Optional.empty();
            }
        }
    }

    public void deleteByBookId(EntityManager em, int bookId) {
        TypedQuery<Request> query = em.createQuery("select r from Request r where r.book.id = :bookId", Request.class);
        query.setParameter("bookId", bookId);
        Request request = query.getSingleResult();
        if (request != null) {
            em.remove(request);
        } else {
            logger.error("Не удалось получить данные запроса на книгу с id = {}", bookId);
        }
        logger.info("Запрос на книгу с id = {} успешно удален", bookId);
    }

    public void increaseAmount(EntityManager em, int id) {
        findById(id).ifPresentOrElse(
                Request::increaseAmount,
                () -> {
                    logger.error("Не удалось увеличить количество запрашиваемых книг в запросе с id = {}: запрос не найден", id);
                }
        );
        logger.info("Количество запрашиваемых книг в запросе с id = {} успешно увеличено", id);
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.requests().exportToCSV(filePath);
    }

    public void importFromCSV(String filePath) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            for (Request request : CSVHandlers.requests().importFromCSV(filePath)) {
                EntityTransaction tx = em.getTransaction();
                try {
                    tx.begin();
                    save(em, request);
                    tx.commit();
                } catch (Exception e) {
                    logger.error("Не удалось импортировать запрос с id = {}: {}", request.getId(), e.getMessage());
                    tx.rollback();
                }
            }
        }

        logger.info("Запросы успешно импортированы из файла '{}'", filePath);
    }

    private RequestRepository() { }
}
