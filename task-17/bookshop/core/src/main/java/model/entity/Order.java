package model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import model.status.IOrderStatus;
import model.status.NewOrderStatus;
import model.enums.OrderStatus;
import model.utils.orm.OrderStatusConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany (fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_book",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> books = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @Column(name = "status")
    @Convert(converter = OrderStatusConverter.class)
    private IOrderStatus status;

    public Order() { }

    public Order(User user) {
        this.createdAt = LocalDate.now();
        this.status = new NewOrderStatus();
        this.user = user;
    }

    public Order(int id, User user, List<Book> books, LocalDate createdAt, LocalDate completedAt, IOrderStatus status) {
        this.id = id;
        this.books = books;
        this.user = user;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.status = status;
    }

    @Override
    public String toString() {
        return "\nOrder{" +
                "id=" + id +
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

    public Integer getId() {
        return id;
    }

    public List<Book> getBooks() {
        return books;
    }

    public User getUser() {
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

    public void setStatus(OrderStatus status) throws IllegalStateException {
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
    }

    public void addBook(Book book) {
        this.books.add(book);
    }

    public boolean areBooksAvailable() {
        return books.stream().allMatch(Book::isAvailable);
    }
}
