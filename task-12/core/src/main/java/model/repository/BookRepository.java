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

    public BookRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Book> findById(int id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            logger.info("Книга с id = {} получена", id);
            return Optional.of(book);
        } else {
            logger.error("Не удалось получить данные книги с id = {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
        logger.info("Список книг успешно получен.");
        return query.getResultList();
    }

    @Override
    public void save(Book obj) {
        if (obj.getId() == null) {
            em.persist(obj);
        } else {
            em.merge(obj);
        }
        logger.info("Книга '{}' успешно добавлена", obj.getName());
    }


    @Override
    public void deleteById(int id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        } else {
            logger.error("Не удалось получить данные книги с id = {}", id);
        }
        logger.info("Книга с id = {} успешно удалена", id);
    }

    public Optional<Book> findByName(String name) {
        try (EntityManager em = JPAConfig.getEntityManager()) {
            TypedQuery<Book> query = em.createQuery("select b from Book b where b.name = :name", Book.class);
            query.setParameter("name", name);
            Book book = query.getSingleResult();
            if (book != null) {
                logger.info("Книга с name = {} получена", name);
                return Optional.of(book);
            } else {
                logger.error("Не удалось получить данные книги с name = {}", name);
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
                logger.error("Не удалось импортировать книгу с id = {}: {}", book.getId(), e.getMessage());
                tx.rollback();
            }
        }

        logger.info("Книги успешно импортированы из файла '{}'", filePath);
    }
}
