package Model.Entity;

import java.time.LocalDateTime;

public class Request {
    private static int nextId = 1;

    private int id;
    private LocalDateTime createdAt;
    private final Book book;
    private int quantity;

    public Request(Book book) {
        this.id = Request.nextId++;
        this.createdAt = LocalDateTime.now();
        this.quantity = 1;

        this.book = book;
    }

    @Override
    public String toString() {
        return "\nModel.Entity.Request{" +
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

    public void increaseAmount() {
        quantity++;
    }
}
