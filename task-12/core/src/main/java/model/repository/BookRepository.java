package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.config.JPAConfig;
import model.entity.Book;
import model.entity.Request;
import model.service.CSVHandler.CSVHandlers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public final class BookRepository implements IRepository<Book> {
    private static final Logger logger = LogManager.getLogger();
    private final EntityManager em;

    private static final String GET_BY_ID_SUCCESS_MSG = "Книга с id = {} получена";
    private static final String GET_BY_ID_ERROR_MSG = "Не удалось получить данные книги с id = {}";
    private static final String GET_BY_NAME_SUCCESS_MSG = "Книга с name = {} получена";
    private static final String GET_BY_NAME_ERROR_MSG = "Не удалось получить данные книги с name = {}";
    private static final String GET_ALL_SUCCESS_MSG = "Список книг успешно получен.";
    private static final String ADD_SUCCESS_MSG = "Книга '{}' успешно добавлена";
    private static final String DELETE_BY_ID_SUCCESS_MSG = "Книга с id = {} успешно удалена";
    private static final String DELETE_BY_ID_ERROR_MSG = "Не удалось получить данные книги с id = {}";
    private static final String IMPORT_SUCCESS_MSG = "Книги успешно импортированы из файла '{}'";
    private static final String IMPORT_ERROR_MSG = "Не удалось импортировать книгу с id = {}: {}";

    public BookRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Book> findById(int id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            logger.debug(GET_BY_ID_SUCCESS_MSG, id);
            return Optional.of(book);
        } else {
            logger.error(GET_BY_ID_ERROR_MSG, id);
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
        logger.debug(GET_ALL_SUCCESS_MSG);
        return query.getResultList();
    }

    @Override
    public void save(Book obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info(ADD_SUCCESS_MSG, obj.getName());
    }


    @Override
    public void deleteById(int id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        } else {
            logger.error(DELETE_BY_ID_ERROR_MSG, id);
        }
        logger.info(DELETE_BY_ID_SUCCESS_MSG, id);
    }

    public Optional<Book> findByName(String name) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            TypedQuery<Book> query = em.createQuery("select b from Book b where b.name = :name", Book.class);
            query.setParameter("name", name);
            Book book = query.getSingleResult();
            if (book != null) {
                logger.info(GET_BY_NAME_SUCCESS_MSG, name);
                return Optional.of(book);
            } else {
                logger.error(GET_BY_NAME_ERROR_MSG, name);
                return Optional.empty();
            }
        }
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.books().exportToCSV(filePath);

    }

    public void importFromCSV(String filePath) {
        for (Book book : CSVHandlers.books().importFromCSV(filePath)) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                save(book);
                tx.commit();
            } catch (Exception e) {
                logger.error(IMPORT_ERROR_MSG, book.getId(), e.getMessage());
                tx.rollback();
            }
        }

        logger.info(IMPORT_SUCCESS_MSG, filePath);
    }
}
