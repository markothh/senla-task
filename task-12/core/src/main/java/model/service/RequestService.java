package model.service;

import jakarta.transaction.Transactional;
import model.entity.Book;
import model.entity.Request;
import model.repository.RequestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    public RequestService(BookService bookService, RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
        this.bookService = bookService;
    }

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
            logger.error(SORT_ERROR_MSG);
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
        }
        requestRepository.save(request);
    }

    @Transactional
    public void createRequest(String bookName) {
        if (bookService.isBookAvailable(bookName)) {
            logger.error(REQUEST_CREATION_AVAILABILITY_ERROR_MSG);
        }

        Book bookToRequest = bookService.getBookByName(bookName);
        createRequestIfNotAvailable(bookToRequest);
        logger.info(REQUEST_CREATION_SUCCESS_MSG, bookName);
    }

    public void exportRequests(String filePath) {
        requestRepository.exportToCSV(filePath);
    }

    @Transactional
    public void importRequests(String filePath) {
        requestRepository.importFromCSV(filePath);
    }
}