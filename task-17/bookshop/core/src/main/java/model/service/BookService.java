package model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.entity.Book;
import model.enums.BookSortField;
import model.repository.BookRepository;
import model.repository.RequestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
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
    private static final String GET_BY_ID_ERROR_MSG = "Книга с id = %d не найдена";
    private static final String GET_BY_NAME_ERROR_MSG = "Книга с названием %s не найдена";
    private static final String GET_DESC_ERROR_MSG = "Невозможно получить описание не существующей книги";
    private static final String ADD_TO_LIST_ERROR_MSG = "Не удалось добавить книгу в список: {}";
    private static final String ORDER_CREATION_ERROR_MSG = "Книги, указанные в заказе, не существуют. Заказ не был создан.";
    private static final String ADD_TO_STOCK_ERROR_MSG = "Не удалось добавить книгу на склад: {}";
    private static final String ADD_TO_STOCK_SUCCESS_MSG = "Книга c id = {} добавлена на склад";
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

    public List<Book> getSortedBooks(BookSortField sortBy, boolean isReversed) {
        HashMap<BookSortField, Comparator<Book>> comparators = new HashMap<>() {{
            put(BookSortField.NAME, Comparator.comparing(Book::getName));
            put(BookSortField.PRICE, Comparator.comparing(Book::getPrice));
            put(BookSortField.PUBLISH_DATE, Comparator.comparing(Book::getPublishYear));
            put(BookSortField.AVAILABILITY, Comparator.comparing(Book::getStockDate, Comparator.nullsLast(LocalDate::compareTo)));
        }};

        Comparator<Book> comparator = comparators.get(sortBy);
        if (comparator == null) {
            throw new NoSuchElementException(SORT_ERROR_MSG);
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
    public Book getBookById(int id) throws NoSuchElementException {
            return bookRepository.findById(id)
                    .orElseThrow(() ->
                            new NoSuchElementException(String.format(GET_BY_ID_ERROR_MSG, id)));
        }

    public String getDescriptionById(int id) {
        return getBookById(id).getDescription();
    }

    public List<Book> formBookListFromIds(List<Integer> ids) throws NoSuchElementException {
        List<Book> result = new ArrayList<>();
        for (int id : ids) {
            try {
                result.add(getBookById(id));
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
    public Book addToStock(int id) {
        Book book = getBookById(id);
        book.setAvailable();
        em.merge(book);

        if (autoCompleteRequests) {
            requestRepository.deleteByBookId(id);
        }
        logger.info(ADD_TO_STOCK_SUCCESS_MSG, id);
        return book;
    }

    public Book removeFromStock(String bookName) {
        Book book = getBookByName(bookName);
        book.setAvailable();
        em.merge(book);

        logger.info(REMOVE_FROM_STOCK_SUCCESS_MSG, bookName);
        return book;
    }

    public boolean isBookAvailable(int id) {
        try {
            return getBookById(id).isAvailable();
        } catch (NoSuchElementException e) {
            logger.error(CHECK_AVAILABILITY_ERROR_MSG, e.getMessage());
            return false;
        }
    }

    public void exportBooks(OutputStream os) {
        bookRepository.exportToCSV(os);
    }

    @Transactional
    public void importBooks(File file) {
        bookRepository.importFromCSV(file);
    }
}
