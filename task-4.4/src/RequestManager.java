import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        Comparator<Request> comparator;
        switch (sortBy) {
            case "quantity":
                comparator =  Comparator.comparing(Request::getQuantity);
                break;
            case "bookName":
                comparator =  Comparator.comparing(Request::getBookName);
                break;
            default:
                throw new IllegalArgumentException("Невозможна сортировка по указанному полю. " +
                        "Возможные значения параметра сортировки: quantity, bookName");
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
