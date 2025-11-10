package Model.Service;

import Model.Entity.Book;
import Model.Entity.Request;

import java.util.*;
import java.util.logging.Logger;

public class RequestManager {
    private List<Request> requests = new ArrayList<>();

    private Optional<Request> getRequestByBook(Book book) {
        return requests.stream()
                .filter(r -> r.getBook() == book)
                .findFirst();
    }

    public List<Request> getRequests() {
        return requests;
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

        return requests.stream()
                .sorted(comparator)
                .toList();
    }

    public Request createRequest(Book book) {
        Optional<Request> optRequest = getRequestByBook(book);
        Request request;
        if (optRequest.isPresent()) {
            request = optRequest.get();
            request.increaseAmount();
        }
        else {
            request = new Request(book);
            requests.add(request);
        }

        System.out.printf("%nСоздан запрос: %s", request);
        System.out.printf("%nАктивные запросы:%n%s", requests);
        return request;
    };

    public void satisfyAllRequestsByBookId(Book book) {
        requests.removeIf(request -> request.getBook() == book);

        System.out.printf("%nУдалены запросы на книгу с id: %d", book.getId());
        System.out.printf("%nАктивные запросы:%n%s", requests);
    }
}
