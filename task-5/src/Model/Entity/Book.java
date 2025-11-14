package Model.Entity;

import Model.Enum.BookStatus;

import java.time.LocalDate;

public class Book {
    private static int nextId = 1;

    private int id;
    private String name;
    private String description;
    private String author;
    private String genre;
    private double price;
    private BookStatus status;
    private int publishYear;
    private LocalDate stockDate;

    private void resetStockDate() {
        stockDate = status == BookStatus.IN_STOCK ? LocalDate.now() : null;
    }

    public Book(String name, String description, String author, String genre, double price, BookStatus status, int publishYear, LocalDate stockDate) {
        this.id = Book.nextId++;
        this.name = name;
        this.description = description;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.status = status;
        this.publishYear = publishYear;
        if (status == BookStatus.IN_STOCK)
            this.stockDate = stockDate;
    }

    @Override
    public String toString() {
        return "\nModel.Entity.Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", publishYear=" + publishYear +
                ", stockDate=" + stockDate +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getStockDate() {
        return stockDate;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setAvailable() {
        this.status = BookStatus.IN_STOCK;
        resetStockDate();
    }

    public void setUnavailable() {
        this.status = BookStatus.OUT_OF_STOCK;
        resetStockDate();
    }

    public boolean isAvailable() {
        return status == BookStatus.IN_STOCK;
    }
}
