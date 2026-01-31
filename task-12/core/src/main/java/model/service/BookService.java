package model.service;

import model.annotations.Inject;
import model.config.DBConnection;
import model.entity.Book;
import model.repository.BookRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public final class BookService {
    private static BookService INSTANCE;
    @Inject
    private BookRepository bookRepository;
    @Inject
    private RequestService requestService;

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
            String errMessage = "Невозможна сортировка по указанному полю. " +
                    "Возможные значения параметра сортировки: bookName, price, publishDate, stockAvailability";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return bookRepository.findAll().stream()
                .sorted(comparator)
                .toList();
    }

    public Book getBookByName(String bookName) {
        return bookRepository.findByName(bookName);
    }

    public String getDescriptionByBookName(String bookName) {
        return getBookByName(bookName).getDescription();
    }

    public List<Book> formIdListFromNames(List<String> names) {
        List<Book> result = new ArrayList<>();
        for (String name : names) {
            try {
                result.add(getBookByName(name));
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
            }
        }

        if (result.isEmpty()) {
            throw new NoSuchElementException("Книги, указанные в заказе, не существуют. Заказ не был создан.");
        }
        return result;
    }

    public void addToStock(String bookName, boolean isRequestSatisfactionNeeded) {
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            getBookByName(bookName).setAvailable();
            if (isRequestSatisfactionNeeded) {
                requestService.satisfyAllRequestsByBookId(getBookByName(bookName).getId());
            }
            System.out.printf("Книга '%s' добавлена на склад", bookName);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }

            String errMessage = String.format("Не удалось добавить книгу на склад: %s", e.getMessage());
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    public void removeFromStock(String bookName) {
        getBookByName(bookName).setUnavailable();
        System.out.printf("Книга '%s' списана со склада", bookName);
    }

    public boolean isBookAvailable(String bookName) {
        return getBookByName(bookName).isAvailable();
    }

    public void exportBooks(String filePath) {
        bookRepository.exportToCSV(filePath);
    }

    public void importBooks(String filePath) {
        bookRepository.importFromCSV(filePath);
    }

    public static BookService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookService();
        }
        return INSTANCE;
    }

    private BookService() { }
}
