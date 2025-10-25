import java.util.ArrayList;
import java.util.List;

public class RequestManager {
    private List<Request> requests = new ArrayList<>();
    public Request createRequest(int bookId){
        Request request = new Request(bookId);
        requests.add(request);

        System.out.printf("%nСоздан запрос: %s", request);
        return request;
    };

    public void satisfyAllRequestsByBookId(int bookId) {
        requests.removeIf(request -> request.getBookId() == bookId);

        System.out.printf("%nУдалены запросы на книгу с id: %d", bookId);
        System.out.printf("%nАктивные запросы:%n%s", requests);
    }
}
