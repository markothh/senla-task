package Model.Entity;

import Model.Entity.DTO.UserProfile;
import Model.Status.IOrderStatus;
import Model.Status.NewOrderStatus;
import Model.Enum.OrderStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private final UserProfile user;
    private List<Book> books = new ArrayList<>();
    private final LocalDate createdAt;
    private LocalDate completedAt;
    private IOrderStatus status;


    public Order(UserProfile user) {
        this.createdAt = LocalDate.now();
        this.status = new NewOrderStatus();

        this.user = user;
    }

    public Order(int id, UserProfile user, List<Book> books, LocalDate createdAt, LocalDate completedAt, IOrderStatus status) {
        this.id = id;
        this.user = user;
        this.books = books;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.status = status;
    }

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

    public UserProfile getUser() {
        return user;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getCompletedAt() {
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

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }

    public void setStatus(OrderStatus status) {
        System.out.printf("%nТекущий статус заказа: %s", this.status);
        switch (status) {
            case NEW:
                this.status = this.status.resetToNew(this);
                break;
            case CANCELLED:
                this.status = this.status.cancel(this);
                break;
            case COMPLETED:
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
