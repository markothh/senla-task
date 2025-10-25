import java.util.Date;

public class Request {
    private static int nextId = 1;

    private int id;
    private Date createdAt;
    private final int bookId;

    public Request(int bookId) {
        this.id = Request.nextId++;
        this.createdAt = new Date();

        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", bookId=" + bookId +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }
}
