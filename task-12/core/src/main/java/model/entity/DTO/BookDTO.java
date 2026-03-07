package model.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import model.entity.Book;

public class BookDTO {
    @NotNull
    private final int id;

    @NotBlank
    private final String name;

    @NotNull
    private final double price;

    public BookDTO(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.price = book.getPrice();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
