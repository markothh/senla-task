package model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import model.entity.Book;
import model.service.CSVHandler.BookCSVHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository implements IRepository<Book> {
    private static final Logger logger = LogManager.getLogger();

    @Lazy
    private final BookCSVHandler csvHandler;

    @PersistenceContext
    private EntityManager em;

    private static final String GET_BY_ID_SUCCESS_MSG = "Книга с id = {} получена";
    private static final String GET_BY_ID_ERROR_MSG = "Не удалось получить данные книги с id = {}";
    private static final String GET_BY_NAME_SUCCESS_MSG = "Книга с name = {} получена";
    private static final String GET_BY_NAME_ERROR_MSG = "Не удалось получить данные книги с name = {}";
    private static final String GET_ALL_SUCCESS_MSG = "Список книг успешно получен.";
    private static final String ADD_SUCCESS_MSG = "Книга '{}' успешно добавлена";
    private static final String DELETE_BY_ID_SUCCESS_MSG = "Книга с id = {} успешно удалена";
    private static final String DELETE_BY_ID_ERROR_MSG = "Не удалось получить данные книги с id = {}";
    private static final String IMPORT_SUCCESS_MSG = "Книги успешно импортированы из файла '{}'";

    public BookRepository(BookCSVHandler csvHandler) {
        this.csvHandler = csvHandler;
    }

    @Override
    public Optional<Book> findById(int id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            logger.info(GET_BY_ID_SUCCESS_MSG, id);
            return Optional.of(book);
        } else {
            logger.error(GET_BY_ID_ERROR_MSG, id);
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
        logger.info(GET_ALL_SUCCESS_MSG);
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

    public void exportToCSV(String filePath) {
        csvHandler.exportToCSV(findAll(), filePath);

    }

    public void importFromCSV(String filePath) {
        for (Book book : csvHandler.importFromCSV(filePath)) {
            em.createNativeQuery("insert into books (" +
                    "id, " +
                    "name, " +
                    "description, " +
                    "author, " +
                    "genre, " +
                    "price, " +
                    "status, " +
                    "publish_year, " +
                    "stock_date) " +
                    "values (:id, :name, :description, :author, :genre, :price, :status, :publish_year, :stock_date) " +
                    "on conflict (id) " +
                    "do update set " +
                    "name = EXCLUDED.name, " +
                    "description = EXCLUDED.description, " +
                    "author = EXCLUDED.author, " +
                    "genre = EXCLUDED.genre, " +
                    "price = EXCLUDED.price, " +
                    "status = EXCLUDED.status, " +
                    "publish_year = EXCLUDED.publish_year, " +
                    "stock_date = EXCLUDED.stock_date")
                    .setParameter("id", book.getId())
                    .setParameter("name", book.getName())
                    .setParameter("description", book.getDescription())
                    .setParameter("author", book.getAuthor())
                    .setParameter("genre", book.getGenre())
                    .setParameter("price", book.getPrice())
                    .setParameter("status", book.getStatus().toString())
                    .setParameter("publish_year", book.getPublishYear())
                    .setParameter("stock_date", book.getStockDate())
                    .executeUpdate();
        }

        logger.info(IMPORT_SUCCESS_MSG, filePath);
    }
}
