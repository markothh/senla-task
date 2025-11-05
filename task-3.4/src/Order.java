import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private static int nextId = 1;

    private int id;
    private int userId;
    private List<Book> books = new ArrayList<>();
    private Date createdAt;
    private IOrderStatus status;

    public Order(int userId) {
        this.id = Order.nextId++;
        this.createdAt = new Date();
        this.status = new NewOrderStatus();

        this.userId = userId;
    };

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", books=" + books +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }

    public int getId() {
        return id;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setStatus(String status) {
        System.out.printf("%nТекущий статус заказа: %s", this.status.getClass());
        switch (status) {
            case "new":
                this.status = this.status.resetToNew(this);
                break;
            case "cancelled":
                this.status = this.status.cancel(this);
                break;
            case "completed":
                this.status = this.status.complete(this);
                break;
            default:
                throw new IllegalStateException("Неверный статус. " +
                        "Докускается использование статусов \"new\", \"cancelled\", \"completed\"");
        }

        System.out.printf("%nCтатус заказа после изменения: %s", this.status.getClass());
    }

    public void addBook(Book book) {
        this.books.add(book);
    }

    public void addBooks(List<Book> books) {
        this.books.addAll(books);
    }

    public boolean areBooksAvailable() {
        return books.stream().allMatch(Book::isAvailable);
    }
}
