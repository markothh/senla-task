package model.service;

import jakarta.transaction.Transactional;
import model.entity.Book;
import model.entity.Request;
import model.enums.RequestSortField;
import model.repository.RequestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RequestService {
    private static final Logger logger = LogManager.getLogger();
    private final RequestRepository requestRepository;
    private final BookService bookService;

    private static final String SORT_ERROR_MSG = "Невозможна сортировка по указанному полю. " +
            "Возможные значения параметра сортировки: quantity, bookName";
    private static final String REQUEST_CREATION_AVAILABILITY_ERROR_MSG = "Невозможно создать заказ на книгу, которая есть в наличии.";
    private static final String REQUEST_CREATION_SUCCESS_MSG = "Успешно создан запрос на книгу '{}'";
    private static final String REQUEST_ID_NOT_FOUND = "Не найден запрос с указанным id";
    private static final String REQUEST_BOOK_ID_NOT_FOUND = "Не найден запрос на книгу с указанным id";

    public RequestService(BookService bookService, RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
        this.bookService = bookService;
    }

    public List<Request> getRequests() {
        return requestRepository.findAll();
    }

    public List<Request> getSortedRequests(RequestSortField sortBy, boolean isReversed) {
        HashMap<RequestSortField, Comparator<Request>> comparators = new HashMap<>() {{
            put(RequestSortField.QUANTITY, Comparator.comparing(Request::getQuantity));
            put(RequestSortField.NAME, Comparator.comparing(Request::getBookName));
        }};

        Comparator<Request> comparator = comparators.get(sortBy);
        if (comparator == null) {
            throw new NoSuchElementException(SORT_ERROR_MSG);
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return requestRepository.findAll().stream()
                .sorted(comparator)
                .toList();
    }

    public Request getRequestById(int id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(REQUEST_ID_NOT_FOUND));
    }

    public Request getRequestByBookId(int id) {
        return requestRepository.findByBookId(id)
                .orElseThrow(() -> new NoSuchElementException(REQUEST_BOOK_ID_NOT_FOUND));
    }

    public Request createRequestIfNotAvailable(Book book) {
        Optional<Request> optRequest = requestRepository.findByBookId(book.getId());
        Request request;
        if (optRequest.isPresent()) {
            request = optRequest.get();
            requestRepository.increaseAmount(request.getId());
        } else {
            request = new Request(book);
        }
        requestRepository.save(request);
        return request;
    }

    @Transactional
    public Request createRequest(int bookId) throws IllegalArgumentException {
        if (bookService.isBookAvailable(bookId)) {
            logger.error(REQUEST_CREATION_AVAILABILITY_ERROR_MSG);
            throw new IllegalArgumentException(REQUEST_CREATION_AVAILABILITY_ERROR_MSG);
        }

        Book bookToRequest = bookService.getBookById(bookId);
        Request request = createRequestIfNotAvailable(bookToRequest);
        logger.info(REQUEST_CREATION_SUCCESS_MSG, bookId);
        return request;
    }

    public void exportRequests(OutputStream os) {
        requestRepository.exportToCSV(os);
    }

    @Transactional
    public void importRequests(File file) {
        requestRepository.importFromCSV(file);
    }
}