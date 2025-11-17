package Model.Service;

import Model.Entity.Book;
import Model.Repository.BookRepository;
import Model.Service.CSVHandler.CSVHandlers;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class BookService {
    private final BookRepository bookRepository = BookRepository.getInstance();

    public List<Book> getBooks() {
        return bookRepository.getBooks();
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

        return bookRepository.getBooks().stream()
                .sorted(comparator)
                .toList();
    }

    public Book getBookByName(String bookName) {
        return bookRepository.getBookByName(bookName);
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

    public void addToStock(String bookName) {
        getBookByName(bookName).setAvailable();
        System.out.printf("Книга '%s' добавлена на склад", bookName);
    }

    public void removeFromStock(String bookName) {
        getBookByName(bookName).setUnavailable();
        System.out.printf("Книга '%s' списана со склада", bookName);
    }

    public boolean isBookAvailable(String bookName) {
        return getBookByName(bookName).isAvailable();
    }

    public void exportBooks(String filePath) {
        bookRepository.exportBooks(filePath);
    }

    public void importBooks(String filePath) {
        bookRepository.importBooks(filePath);
    }
}
