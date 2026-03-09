package model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "quantity")
    private int quantity;

    public Request() { }

    public Request(Book book) {
        this.createdAt = LocalDate.now();
        this.quantity = 1;

        this.book = book;
    }

    public Request(int id, LocalDate createdAt, Book book, int quantity) {
        this.id = id;
        this.createdAt = createdAt;
        this.book = book;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "\nRequest{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", \n\tbook=" + book +
                ", \n\tquantity=" + quantity +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public String getBookName() {
        return book.getName();
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void increaseAmount() {
        quantity = quantity + 1;
    }
}
