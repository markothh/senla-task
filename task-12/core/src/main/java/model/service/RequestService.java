package model.service;

import model.annotations.Inject;
import model.entity.Book;
import model.entity.Request;
import model.repository.RequestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class RequestService {
    private static final Logger logger = LogManager.getLogger();
    private static RequestService INSTANCE;
    @Inject
    private RequestRepository requestRepository;
    @Inject
    private BookService bookService;

    public List<Request> getRequests() {
        return requestRepository.findAll();
    }

    public List<Request> getSortedRequests(String sortBy, boolean isReversed) {
        HashMap<String, Comparator<Request>> comparators = new HashMap<>() {{
            put("quantity", Comparator.comparing(Request::getQuantity));
            put("bookName", Comparator.comparing(Request::getBookName));
        }};

        Comparator<Request> comparator = comparators.get(sortBy);
        if (comparator == null) {
            logger.error("Невозможна сортировка по указанному полю. " +
                    "Возможные значения параметра сортировки: quantity, bookName");
            return List.of();
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return requestRepository.findAll().stream()
                .sorted(comparator)
                .toList();
    }

    public void createRequestIfNotAvailable(Book book) {
        Optional<Request> optRequest = requestRepository.findByBookId(book.getId());
        Request request;
        if (optRequest.isPresent()) {
            request = optRequest.get();
            requestRepository.increaseAmount(request.getId());
        } else {
            request = new Request(book);
            requestRepository.save(request);
        }
    }

    public void createRequest(String bookName) {
        if (bookService.isBookAvailable(bookName)) {
            logger.error("Невозможно создать заказ на книгу, которая есть в наличии.");
        }

        try {
            Book bookToRequest = bookService.getBookByName(bookName);
            createRequestIfNotAvailable(bookToRequest);
        } catch (NoSuchElementException e) {
            logger.error("Не удалось создать запрос на книгу '{}': {}", bookName, e.getMessage());
        }
    }

    public void satisfyAllRequestsByBookId(int bookId) {
        requestRepository.deleteByBookId(bookId);
    }

    public void exportRequests(String filePath) {
        requestRepository.exportToCSV(filePath);
    }

    public void importRequests(String filePath) {
        requestRepository.importFromCSV(filePath);
    }

    public static RequestService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestService();
        }
        return INSTANCE;
    }

    private RequestService() { }
}