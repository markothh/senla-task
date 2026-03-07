package model.entity.DTO;

import jakarta.validation.constraints.NotNull;
import model.entity.Book;
import model.entity.Order;
import java.util.List;

public class OrderCreationDTO {
    @NotNull
    private int userId;

    @NotNull
    private List<Integer> books;

    public OrderCreationDTO() { }

    public OrderCreationDTO(Order order) {
        this.userId = order.getUser().getId();
        this.books = order.getBooks().stream()
                .map(Book::getId)
                .toList();
    }

    public int getUserId() {
        return userId;
    }

    public List<Integer> getBooks() {
        return books;
    }
}
