package model.entity.DTO;

import jakarta.validation.constraints.NotNull;
import model.entity.Book;
import model.entity.Order;
import java.util.List;

public class OrderCreationDTO {
    @NotNull
    private List<Integer> books;

    public OrderCreationDTO() { }

    public OrderCreationDTO(Order order) {
        this.books = order.getBooks().stream()
                .map(Book::getId)
                .toList();
    }

    public List<Integer> getBooks() {
        return books;
    }
}
