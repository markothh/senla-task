package Model.Service;

import Model.Entity.Book;
import Model.Entity.Request;
import Model.Repository.RequestRepository;

import java.util.*;
import java.util.logging.Logger;

public class RequestService {
    private final RequestRepository requestRepository = RequestRepository.getInstance();

    public List<Request> getRequests() {
        return requestRepository.getRequests();
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

        return requestRepository.getRequests().stream()
                .sorted(comparator)
                .toList();
    }

    public Request createRequest(Book book) {
        Optional<Request> optRequest = requestRepository.getRequestByBook(book);
        Request request;
        if (optRequest.isPresent()) {
            request = optRequest.get();
            request.increaseAmount();
        }
        else {
            request = new Request(book);
            requestRepository.save(request);
        }

        System.out.printf("%nСоздан запрос: %s", request);
        System.out.printf("%nАктивные запросы:%n%s", requestRepository.getRequests());
        return request;
    }

    public void satisfyAllRequestsByBook(Book book) {
        requestRepository.deleteRequestsByBook(book);

        System.out.printf("%nУдалены запросы на книгу с id: %d", book.getId());
        System.out.printf("%nАктивные запросы:%n%s", requestRepository.getRequests());
    }

    public void exportRequests(String filePath) {
        requestRepository.exportToCSV(filePath);
    }

    public void importRequests(String filePath) {
        requestRepository.importToCSV(filePath);

    }
}
