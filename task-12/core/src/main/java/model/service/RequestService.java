package model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.annotations.Inject;
import model.config.JPAConfig;
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
    private final EntityManager em;
    private final RequestRepository requestRepository = new RequestRepository(JPAConfig.getEntityManager());

    public RequestService(EntityManager em) {
        this.em = em;
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

    public void createRequestIfNotAvailable(EntityManager em, Book book) {
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
        BookService bookService = new BookService(em);
        if (bookService.isBookAvailable(bookName)) {
            logger.error("Невозможно создать заказ на книгу, которая есть в наличии.");
        }

        try {
            Book bookToRequest = bookService.getBookByName(bookName);
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                createRequestIfNotAvailable(em, bookToRequest);
                tx.commit();
            } catch (Exception e) {
                logger.error("Не удалось создать запрос на книгу '{}': {}", bookName, e.getMessage());
                tx.rollback();
            }

        } catch (NoSuchElementException e) {
            logger.error("Не удалось создать запрос на книгу '{}': {}", bookName, e.getMessage());
        }
    }

    public void satisfyAllRequestsByBookId(EntityManager em, int bookId) {
        requestRepository.deleteByBookId(bookId);
    }

    public void exportRequests(String filePath) {
        requestRepository.exportToCSV(filePath);
    }

    public void importRequests(String filePath) {
        requestRepository.importFromCSV(filePath);
    }
}