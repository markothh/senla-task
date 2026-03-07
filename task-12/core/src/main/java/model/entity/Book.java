package model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import model.enums.BookStatus;

import java.time.LocalDate;


@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "author")
    private String author;

    @Column(name = "genre")
    private String genre;

    @Column(name = "price")
    private double price;

    @Column(name = "publish_year")
    private int publishYear;

    @Column(name = "stock_date")
    private LocalDate stockDate;

    private void resetStockDate() {
        stockDate = status == BookStatus.IN_STOCK ? LocalDate.now() : null;
    }

    public Book() { }

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
