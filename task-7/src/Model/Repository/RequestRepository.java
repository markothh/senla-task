package Model.Repository;

import Model.Entity.Book;
import Model.Entity.Request;
import Model.Service.CSVHandler.CSVHandlers;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class RequestRepository implements Serializable {
    private List<Request> requests = new ArrayList<>();
    private static RequestRepository INSTANCE;

    public static RequestRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestRepository();
        }
        return INSTANCE;
    }

    public Optional<Request> getRequestByBook(Book book) {
        return requests.stream()
                .filter(r -> r.getBook() == book)
                .findFirst();
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setData(List<Request> requests) {
        this.requests = requests;
    }

    public void deleteRequestsByBook(Book book) {
        requests.removeIf(request -> request.getBook() == book);
    }

    public void save(Request request) {
        requests.add(request);
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.requests().exportToCSV(filePath);
    }

    public void importToCSV(String filePath) {
        Map<Integer, Request> merged = new HashMap<>();
        for (Request request : requests) {
            merged.put(request.getId(), request);
        }

        for (Request request : CSVHandlers.requests().importFromCSV(filePath)) {
            merged.put(request.getId(), request);
        }
        requests = new ArrayList<>(merged.values());
    }

    @Serial
    private Object readResolve() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        else {
            INSTANCE.requests = this.requests;
        }
        return INSTANCE;
    }
}
