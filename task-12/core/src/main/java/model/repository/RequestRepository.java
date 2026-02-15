package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.config.JPAConfig;
import model.entity.Request;
import model.service.CSVHandler.CSVHandlers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class RequestRepository implements IRepository<Request> {
    private static final Logger logger = LogManager.getLogger();
    private final EntityManager em;

    private static final String GET_BY_ID_SUCCESS_MSG = "Запрос с id = {} получен";
    private static final String GET_BY_ID_ERROR_MSG = "Не удалось получить данные запроса с id = {}";
    private static final String GET_BY_BOOK_ID_SUCCESS_MSG = "Успешно получены запросы на книгу с id = {}";
    private static final String GET_BY_BOOK_ID_ERROR_MSG = "Запросы на книгу с id = {} не найдены";
    private static final String GET_ALL_SUCCESS_MSG = "Список запросов успешно получен.";
    private static final String ADD_SUCCESS_MSG = "Запрос '{}' успешно добавлена";
    private static final String DELETE_BY_ID_SUCCESS_MSG = "Запрос на книгу с id = {} успешно удален";
    private static final String DELETE_BY_ID_ERROR_MSG = "Не удалось получить данные запроса на книгу с id = {}";
    private static final String DELETE_BY_BOOK_ID_SUCCESS_MSG = "Запрос с id = {} успешно удален";
    private static final String DELETE_BY_BOOK_ID_ERROR_MSG = "Не удалось получить данные запроса с id = {}";
    private static final String IMPORT_SUCCESS_MSG = "Запросы успешно импортированы из файла '{}'";
    private static final String IMPORT_ERROR_MSG = "Не удалось импортировать запрос с id = {}: {}";
    private static final String INCREASE_AMOUNT_SUCCESS_MSG = "Не удалось увеличить количество запрашиваемых книг в запросе с id = {}: запрос не найден";
    private static final String INCREASE_AMOUNT_ERROR_MSG = "Количество запрашиваемых книг в запросе с id = {} успешно увеличено";

    public RequestRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Request> findById(int id) {
        Request request = em.find(Request.class, id);
        if (request != null) {
            logger.debug(GET_BY_ID_SUCCESS_MSG, id);
            return Optional.of(request);
        } else {
            logger.error(GET_BY_ID_ERROR_MSG, id);
            return Optional.empty();
        }
    }

    @Override
    public List<Request> findAll() {
        TypedQuery<Request> query = em.createQuery(
                "select r from Request r " +
                        "join fetch r.book", Request.class);
        logger.debug(GET_ALL_SUCCESS_MSG);
        return query.getResultList();
    }

    @Override
    public void save(Request obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info(ADD_SUCCESS_MSG);
    }

    @Override
    public void deleteById(int id) {
        Request request = em.find(Request.class, id);
        if (request != null) {
            em.remove(request);
        } else {
            logger.error(DELETE_BY_ID_ERROR_MSG, id);
        }
        logger.info(DELETE_BY_ID_SUCCESS_MSG, id);
    }

    public Optional<Request> findByBookId(int bookId) {
        TypedQuery<Request> query = em.createQuery("select r from Request r where r.book.id = :bookId", Request.class);
        query.setParameter("bookId", bookId);
        try {
            Request request = query.getSingleResult();

            logger.info(GET_BY_BOOK_ID_SUCCESS_MSG, bookId);
            return Optional.of(request);
        } catch (Exception e) {
            logger.error(GET_BY_BOOK_ID_ERROR_MSG, bookId);
            return Optional.empty();
        }
    }

    public void deleteByBookId(int bookId) {
        TypedQuery<Request> query = em.createQuery("select r from Request r where r.book.id = :bookId", Request.class);
        query.setParameter("bookId", bookId);
        Request request = query.getSingleResult();
        if (request != null) {
            em.remove(request);
        } else {
            logger.error(DELETE_BY_BOOK_ID_ERROR_MSG, bookId);
        }
        logger.info(DELETE_BY_BOOK_ID_SUCCESS_MSG, bookId);
    }

    public void increaseAmount(int id) {
        findById(id).ifPresentOrElse(
                Request::increaseAmount,
                () -> {
                    logger.error(INCREASE_AMOUNT_ERROR_MSG, id);
                }
        );
        logger.info(INCREASE_AMOUNT_SUCCESS_MSG, id);
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.requests().exportToCSV(filePath);
    }

    public void importFromCSV(String filePath) {
        for (Request request : CSVHandlers.requests().importFromCSV(filePath)) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                save(request);
                tx.commit();
            } catch (Exception e) {
                logger.error(IMPORT_ERROR_MSG, request.getId(), e.getMessage());
                tx.rollback();
            }
        }

        logger.info(IMPORT_SUCCESS_MSG, filePath);
    }
}
