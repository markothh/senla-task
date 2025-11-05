import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Order {
    private static int nextId = 1;
    private static final Logger logger = Logger.getLogger(Order.class.getName());

    private int id;
    private User user;
    private List<Book> books = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private IOrderStatus status;


    public Order(User user) {
        this.id = Order.nextId++;
        this.createdAt = LocalDateTime.now();
        this.status = new NewOrderStatus();

        this.user = user;
    };

    @Override
    public String toString() {
        return "\nOrder{" +
                "id=" + id +
                ", user=" + user +
                ", \n\tbooks=" + books +
                ", \n\tcreatedAt=" + createdAt +
                ", completedAt=" + completedAt +
                ", status=" + status +
                ", sum=" + getSum() +
                '}';
    }

    public String getInfo() {
        return String.format("Заказ №%d:%nЗаказчик: %s, Заказанные книги:%n" + books.stream().map(Book::getName).toList(),
                id, user.getId());
    }

    public int getId() {
        return id;
    }

    public List<Book> getBooks() {
        return books;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public double getSum() {
        return books.stream()
                .mapToDouble(Book::getPrice)
                .sum();
    }

    public String getStatus() {
        return status.toString();
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setStatus(OrderStatus status) {
        System.out.printf("%nТекущий статус заказа: %s", this.status);
        switch (status) {
            case OrderStatus.NEW:
                this.status = this.status.resetToNew(this);
                break;
            case OrderStatus.CANCELLED:
                this.status = this.status.cancel(this);
                break;
            case OrderStatus.COMPLETED:
                this.status = this.status.complete(this);
                break;
        }

        System.out.printf("%nCтатус заказа после изменения: %s", this.status);
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
