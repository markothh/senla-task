package model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.annotations.Inject;
import model.config.JPAConfig;
import model.entity.Book;
import model.repository.BookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public final class BookService {
    private static final Logger logger = LogManager.getLogger();
    private final EntityManager em;
    private final BookRepository bookRepository = new BookRepository(JPAConfig.getEntityManager());

    public BookService(EntityManager em) {
        this.em = em;
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getSortedBooks(String sortBy, boolean isReversed) {
        HashMap<String, Comparator<Book>> comparators = new HashMap<>() {{
            put("bookName", Comparator.comparing(Book::getName));
            put("price", Comparator.comparing(Book::getPrice));
            put("publishDate", Comparator.comparing(Book::getPublishYear));
            put("stockAvailability", Comparator.comparing(Book::getStockDate, Comparator.nullsLast(LocalDate::compareTo)));
        }};

        Comparator<Book> comparator = comparators.get(sortBy);
        if (comparator == null) {
            logger.error("Невозможна сортировка по указанному полю. " +
                    "Возможные значения параметра сортировки: bookName, price, publishDate, stockAvailability");
            return List.of();
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return bookRepository.findAll().stream()
                .sorted(comparator)
                .toList();
    }

    public Book getBookByName(String bookName) throws NoSuchElementException {
        return bookRepository.findByName(bookName)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Книга с названием %s не найдена", bookName)));
    }

    public String getDescriptionByBookName(String bookName) {
        try {
            return getBookByName(bookName).getDescription();
        } catch (NoSuchElementException e) {
            logger.error("Невозможно получить описание не существующей книги");
            return "";
        }
    }

    public List<Book> formIdListFromNames(List<String> names) throws NoSuchElementException {
        List<Book> result = new ArrayList<>();
        for (String name : names) {
            try {
                result.add(getBookByName(name));
            } catch (NoSuchElementException e) {
                logger.error("Не удалось добавить книгу в список: {}", e.getMessage());
            }
        }

        if (result.isEmpty()) {
            throw new NoSuchElementException("Книги, указанные в заказе, не существуют. Заказ не был создан.");
        }
        return result;
    }

    public void addToStock(String bookName, boolean isRequestSatisfactionNeeded) {
        RequestService requestService = new RequestService(em);
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            try {
                Book book = getBookByName(bookName);
                book.setAvailable();
                em.merge(book);
            } catch (NoSuchElementException e) {
                logger.error("Не удалось добавить книгу на склад: {}", e.getMessage());
            }

            if (isRequestSatisfactionNeeded) {
                requestService.satisfyAllRequestsByBookId(em, getBookByName(bookName).getId());
            }
            logger.info("Книга '{}' добавлена на склад", bookName);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            logger.error("Не удалось добавить книгу '{}' на склад: {}", bookName, e.getMessage());
        }
    }

    public void removeFromStock(String bookName) {
        try {
            getBookByName(bookName).setUnavailable();
        } catch (NoSuchElementException e) {
            logger.error("Не удалось списать книгу со склада: {}", e.getMessage());
        }
        logger.info("Книга '{}' списана со склада", bookName);
    }

    public boolean isBookAvailable(String bookName) {
        try {
            return getBookByName(bookName).isAvailable();
        } catch (NoSuchElementException e) {
            logger.error("Не удалось проверить наличие книги: {}", e.getMessage());
            return false;
        }
    }

    public void exportBooks(String filePath) {
        bookRepository.exportToCSV(filePath);
    }

    public void importBooks(String filePath) {
        bookRepository.importFromCSV(filePath);
    }
}
