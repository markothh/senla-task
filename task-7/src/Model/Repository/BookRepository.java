package Model.Repository;

import Model.Entity.Book;
import Model.Service.CSVHandler.CSVHandlers;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class BookRepository implements Serializable {
    private static BookRepository INSTANCE;
    private List<Book> books = new ArrayList<>();

    public static BookRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookRepository();
        }
        return INSTANCE;
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getBookById(int bookId) {
        return findBook(b -> b.getId() == bookId);
    }

    public Book getBookByName(String bookName) {
        return findBook(b -> b.getName().equals(bookName));
    }

    public void setData(List<Book> books) {
        this.books = books;
    }

    public void exportBooks(String filePath) {
        CSVHandlers.books().exportToCSV(filePath);
    }

    public void importBooks(String filePath) {
        Map<Integer, Book> merged = new HashMap<>();
        for (Book book : books) {
            merged.put(book.getId(), book);
        }

        for (Book book : CSVHandlers.books().importFromCSV(filePath)) {
            merged.put(book.getId(), book);
        }
        books = new ArrayList<>(merged.values());
    }

    private BookRepository() {}

    @Serial
    private Object readResolve() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        else {
            INSTANCE.books = this.books;
        }
        return INSTANCE;
    }

    private Book findBook(Predicate<Book> filter) {
        return books.stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(() -> {
                    String errMessage = "Книга с указанными данными не найдена";
                    Logger.getGlobal().severe(errMessage);
                    return new NoSuchElementException(errMessage);
                });
    }
}
