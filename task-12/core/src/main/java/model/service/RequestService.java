package model.service;

import model.annotations.Inject;
import model.entity.Book;
import model.entity.Request;
import model.repository.RequestRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public final class RequestService {
    private static RequestService INSTANCE;
    @Inject
    private RequestRepository requestRepository;

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
            String errMessage = "Невозможна сортировка по указанному полю. " +
                    "Возможные значения параметра сортировки: quantity, bookName";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        if (isReversed) {
            comparator = comparator.reversed();
        }

        return requestRepository.findAll().stream()
                .sorted(comparator)
                .toList();
    }

    public Request createRequest(Book book) {
        Optional<Request> optRequest = requestRepository.findByBookId(book.getId());
        Request request;
        if (optRequest.isPresent()) {
            request = optRequest.get();
            requestRepository.increaseAmount(request.getId());
        } else {
            request = new Request(book);
            requestRepository.save(request);
        }

        System.out.printf("%nСоздан запрос: %s", request);
        System.out.printf("%nАктивные запросы:%n%s", requestRepository.findAll());
        return request;
    }

    public void satisfyAllRequestsByBookId(int bookId) {
        requestRepository.deleteByBookId(bookId);

        System.out.printf("%nУдалены запросы на книгу с id: %d", bookId);
        System.out.printf("%nАктивные запросы:%n%s", requestRepository.findAll());
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