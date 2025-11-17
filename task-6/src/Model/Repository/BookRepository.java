package Model.Repository;

import Model.Entity.Book;
import Model.Service.CSVHandler.CSVHandlers;

import java.util.*;
import java.util.logging.Logger;

public class BookRepository {
    private static BookRepository INSTANCE;
    private List<Book> books = new ArrayList<>();

    public static BookRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookRepository();
        }
        return INSTANCE;
    }

    public Book getBookById(int bookId) {
        return books.stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> {
                    String errMessage = String.format("Книга с указанным id = %d не найдена", bookId);
                    Logger.getGlobal().severe(errMessage);
                    return new NoSuchElementException(errMessage);
                });
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getBookByName(String bookName) {
        return books.stream()
                .filter(b -> b.getName().equals(bookName))
                .findFirst()
                .orElseThrow(() -> {
                    String errMessage = String.format("Книга с указанным id = %s не найдена", bookName);
                    Logger.getGlobal().severe(errMessage);
                    return new NoSuchElementException(errMessage);
                });
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

    private BookRepository() {};
}
