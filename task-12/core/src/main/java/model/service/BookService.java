package model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.entity.Book;
import model.repository.BookRepository;
import model.repository.RequestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {
    private static final Logger logger = LogManager.getLogger();
    private final BookRepository bookRepository;
    private final RequestRepository requestRepository;
    @Value("${autoCompleteRequests}")
    private boolean autoCompleteRequests;

    @PersistenceContext
    private EntityManager em;

    private static final String SORT_ERROR_MSG = "Невозможна сортировка по указанному полю. " +
            "Возможные значения параметра сортировки: bookName, price, publishDate, stockAvailability";
    private static final String GET_BY_NAME_ERROR_MSG = "Книга с названием %s не найдена";
    private static final String GET_DESC_ERROR_MSG = "Невозможно получить описание не существующей книги";
    private static final String ADD_TO_LIST_ERROR_MSG = "Не удалось добавить книгу в список: {}";
    private static final String ORDER_CREATION_ERROR_MSG = "Книги, указанные в заказе, не существуют. Заказ не был создан.";
    private static final String ADD_TO_STOCK_ERROR_MSG = "Не удалось добавить книгу на склад: {}";
    private static final String ADD_TO_STOCK_SUCCESS_MSG = "Книга '{}' добавлена на склад";
    private static final String REMOVE_FROM_STOCK_ERROR_MSG = "Не удалось списать книгу со склада: {}";
    private static final String REMOVE_FROM_STOCK_SUCCESS_MSG = "Книга '{}' списана со склада";
    private static final String CHECK_AVAILABILITY_ERROR_MSG = "Не удалось проверить наличие книги: {}";

    public BookService(BookRepository bookRepository, RequestRepository requestRepository) {
        this.bookRepository = bookRepository;
        this.requestRepository = requestRepository;
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
            logger.error(SORT_ERROR_MSG);
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
                        new NoSuchElementException(String.format(GET_BY_NAME_ERROR_MSG, bookName)));
    }

    public String getDescriptionByBookName(String bookName) {
        try {
            return getBookByName(bookName).getDescription();
        } catch (NoSuchElementException e) {
            logger.error(GET_DESC_ERROR_MSG);
            return "";
        }
    }

    public List<Book> formIdListFromNames(List<String> names) throws NoSuchElementException {
        List<Book> result = new ArrayList<>();
        for (String name : names) {
            try {
                result.add(getBookByName(name));
            } catch (NoSuchElementException e) {
                logger.error(ADD_TO_LIST_ERROR_MSG, e.getMessage());
            }
        }

        if (result.isEmpty()) {
            throw new NoSuchElementException(ORDER_CREATION_ERROR_MSG);
        }
        return result;
    }

    @Transactional
    public void addToStock(String bookName) {
        Book book;
        try {
            book = getBookByName(bookName);
            book.setAvailable();
            em.merge(book);
        } catch (NoSuchElementException e) {
            logger.error(ADD_TO_STOCK_ERROR_MSG, e.getMessage());
            return;
        }

        if (autoCompleteRequests) {
            requestRepository.deleteByBookId(book.getId());
        }
        logger.info(ADD_TO_STOCK_SUCCESS_MSG, bookName);
    }

    public void removeFromStock(String bookName) {
        try {
            Book book = getBookByName(bookName);
            book.setAvailable();
            em.merge(book);
        } catch (NoSuchElementException e) {
            logger.error(REMOVE_FROM_STOCK_ERROR_MSG, e.getMessage());
        }
        logger.info(REMOVE_FROM_STOCK_SUCCESS_MSG, bookName);
    }

    public boolean isBookAvailable(String bookName) {
        try {
            return getBookByName(bookName).isAvailable();
        } catch (NoSuchElementException e) {
            logger.error(CHECK_AVAILABILITY_ERROR_MSG, e.getMessage());
            return false;
        }
    }

    public void exportBooks(String filePath) {
        bookRepository.exportToCSV(filePath);
    }

    @Transactional
    public void importBooks(String filePath) {
        bookRepository.importFromCSV(filePath);
    }
}
