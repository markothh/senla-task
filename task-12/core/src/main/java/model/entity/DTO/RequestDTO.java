package model.entity.DTO;

import jakarta.validation.constraints.NotNull;
import model.entity.Request;

import java.time.LocalDate;

public class RequestDTO {
    private int id;

    @NotNull
    private LocalDate createdAt;

    @NotNull
    private BookDTO book;

    @NotNull
    private int quantity;

    public RequestDTO(Request request) {
        this.id = request.getId();
        this.createdAt = request.getCreatedAt();
        this.book = new BookDTO(request.getBook());
        this.quantity = request.getQuantity();
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public BookDTO getBook() {
        return book;
    }
}
