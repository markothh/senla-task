package model.entity;

import model.enums.BookStatus;

import java.time.LocalDate;

public class Book {
    private final int id;
    private final String name;
    private final String description;
    private final String author;
    private final String genre;
    private final double price;
    private BookStatus status;
    private final int publishYear;
    private LocalDate stockDate;

    private void resetStockDate() {
        stockDate = status == BookStatus.IN_STOCK ? LocalDate.now() : null;
    }

    public Book(int id, String name, String description, String author, String genre, double price, BookStatus status, int publishYear, LocalDate stockDate) {
        this.id = id;
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
        return "\nBook{" +
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

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public BookStatus getStatus() {
        return status;
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
