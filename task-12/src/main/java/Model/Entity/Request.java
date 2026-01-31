package Model.Entity;

import java.time.LocalDate;

public class Request {
    private int id;
    private final LocalDate createdAt;
    private final Book book;
    private final int quantity;

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

    public int getId() {
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
}
