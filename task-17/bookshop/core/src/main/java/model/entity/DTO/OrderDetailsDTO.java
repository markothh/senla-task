package model.entity.DTO;

import jakarta.validation.constraints.NotNull;
import model.entity.Order;
import model.enums.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public class OrderDetailsDTO {
    @NotNull
    private int id;

    @NotNull
    private int userId;

    @NotNull
    private List<BookDTO> books;

    @NotNull
    private LocalDate createdAt;
    private LocalDate completedAt;

    @NotNull
    private OrderStatus status;

    public OrderDetailsDTO(Order order) {
        this.id = order.getId();;
        this.userId = order.getUser().getId();
        this.createdAt = order.getCreatedAt();
        this.completedAt = order.getCompletedAt();
        this.books = order.getBooks().stream()
                .map(BookDTO::new)
                .toList();
        this.status = OrderStatus.valueOf(order.getStatus());
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public List<BookDTO> getBooks() {
        return books;
    }
}
