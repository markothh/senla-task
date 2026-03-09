package model.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import model.entity.Book;
import model.enums.BookStatus;

import java.time.LocalDate;

public class BookDetailsDTO {
    @NotNull
    private final int id;

    @NotBlank
    private final String name;

    private final String description;

    @NotBlank
    private final String author;
    private final String genre;

    @NotNull
    private final double price;

    private final BookStatus status;
    private final int publishYear;
    private final LocalDate stockDate;

    public BookDetailsDTO(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.description = book.getDescription();
        this.author = book.getAuthor();
        this.genre = book.getGenre();
        this.price = book.getPrice();
        this.status = book.getStatus();
        this.publishYear = book.getPublishYear();
        this.stockDate = book.getStockDate();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public double getPrice() {
        return price;
    }

    public BookStatus getStatus() {
        return status;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public LocalDate getStockDate() {
        return stockDate;
    }
}
