package model.repository;

import model.config.DBConnection;
import model.entity.Book;
import model.entity.Order;
import model.enums.OrderStatus;
import model.service.CSVHandler.CSVHandlers;
import model.utils.EntityParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class OrderRepository implements Serializable, IRepository<Order> {
    private static final Logger logger = LogManager.getLogger();
    private static OrderRepository INSTANCE;

    private OrderRepository() { }

    public static OrderRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderRepository();
        }
        return INSTANCE;
    }

    @Override
    public void save(Order order) {
        try {
            int orderId = saveOrderData(order);
            for (Book book : order.getBooks()) {
                saveOrderBook(orderId, book.getId());
            }
            logger.info("Заказ успешно добавлен. Ему присвовен номер {}", orderId);
        } catch (SQLException e) {
            logger.error("Не удалось сформировать заказ: {}", e.getMessage());
        }
    }

    @Override
    public List<Order> findAll() {
        Map<Integer, Order> ordersData = new HashMap<>();
        try (var stmt = DBConnection.getInstance().getConnection().createStatement()) {
            try (var rs = stmt.executeQuery("select " +
                    "o.id as order_id, " +
                    "o.status as order_status, " +
                    "o.created_at as order_created_at, " +
                    "o.completed_at as order_completed_at, " +
                    "u.id as user_id, " +
                    "u.name as user_name, " +
                    "b.id as book_id, " +
                    "b.name as book_name, " +
                    "b.description as book_description, " +
                    "b.author as book_author, " +
                    "b.genre as book_genre, " +
                    "b.price as book_price, " +
                    "b.status as book_status, " +
                    "b.publish_year as book_publish_year, " +
                    "b.stock_date as book_stock_date " +
                    "from orders o " +
                    "join users u on u.id = o.user_id " +
                    "join order_book ob on ob.order_id = o.id " +
                    "join books b on b.id = ob.book_id ")) {
                while (rs.next()) {
                    try {
                        int orderId = rs.getInt("order_id");
                        Order order = ordersData.computeIfAbsent(
                                orderId,
                                id -> EntityParser.parseOrder(rs)
                        );

                        order.getBooks().add(EntityParser.parseBook(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные заказа не удалось извлечь из БД: {}", e.getMessage());
                    }

                }
                logger.info("Список заказов успешно получен");
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить список заказов: {}", e.getMessage());
        }
        return new ArrayList<>(ordersData.values());
    }

    @Override
    public Optional<Order> findById(int orderId) {
        try (var stmt = DBConnection.getInstance().getConnection().prepareStatement("select " +
                "o.id as order_id, " +
                "o.status as order_status, " +
                "o.created_at as order_created_at, " +
                "o.completed_at as order_completed_at, " +
                "u.id as user_id, " +
                "u.name as user_name, " +
                "b.id as book_id, " +
                "b.name as book_name, " +
                "b.description as book_description, " +
                "b.author as book_author, " +
                "b.genre as book_genre, " +
                "b.price as book_price, " +
                "b.status as book_status, " +
                "b.publish_year as book_publish_year, " +
                "b.stock_date as book_stock_date " +
                "from orders o " +
                "join users u on u.id = o.user_id " +
                "join order_books ob on ob.order_id = o.id " +
                "join books b on b.id = ob.book_id " +
                "where o.id = ?")) {
            stmt.setInt(1, orderId);
            try (var rs = stmt.executeQuery()) {
                Order order = null;
                while (rs.next()) {
                    try {
                        if (order == null) {
                            order = EntityParser.parseOrder(rs);
                        }
                        order.getBooks().add(EntityParser.parseBook(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные заказа не удалось извлечь из БД: {}", e.getMessage());
                    }
                }

                if (order != null) {
                    logger.info("Успешно получены данные заказа с id = {}", orderId);
                    return Optional.of(order);
                } else {
                    logger.error("Не удалось получить данные заказа с id = {}", orderId);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить данные заказа с id = {}: {}", orderId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(int id) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("delete from orders " +
                        "where id = ?")) {
            stmt.setInt(1, id);

            stmt.execute();
            logger.info("Заказ с id = {} успешно удален", id);
        } catch (SQLException e) {
            logger.error("Не удалось удалить заказ с id = {}", id);
        }
    }

    public String getOrderInfo(int orderId) {
        return findById(orderId)
                .map(Order::getInfo)
                .orElse("");
    }

    public void setOrderStatus (int orderId, OrderStatus status) throws IllegalStateException {
        findById(orderId)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Заказ с номером %d не найден", orderId)))
                .setStatus(status);
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.orders().exportToCSV(filePath);
    }

    public void importFromCSV(String filePath) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into orders (" +
                        "id, " +
                        "user_id, " +
                        "created_at, " +
                        "completed_at, " +
                        "status)" +
                        "values (?, ?, ?, ?, ?) " +
                        "on conflict (id) " +
                        "do update set " +
                        "user_id = EXCLUDED.user_id, " +
                        "created_at = EXCLUDED.created_at, " +
                        "completed_at = EXCLUDED.completed_at, " +
                        "status = EXCLUDED.status")) {
            for (Order order : CSVHandlers.orders().importFromCSV(filePath)) {
                stmt.setInt(1, order.getId());
                stmt.setInt(2, order.getUser().getId());
                LocalDate createdAt = order.getCreatedAt();
                if (createdAt != null) {
                    stmt.setDate(3, Date.valueOf(createdAt));
                }
                LocalDate completedAt = order.getCompletedAt();
                if (createdAt != null) {
                    stmt.setDate(4, Date.valueOf(createdAt));
                }
                stmt.setString(5, order.getStatus());

                stmt.addBatch();
            }

            stmt.executeBatch();
            logger.info("Заказы успешно импортированы из файла '{}'", filePath);
        } catch (SQLException e) {
            logger.error("Не удалось импортировать заказы из файла '{}'. Подробнее: {}", filePath, e.getMessage());
        }
    }

    private void saveOrderBook(int orderId, int bookId) throws SQLException {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into order_book (" +
                        "order_id, " +
                        "book_id, " +
                        "quantity) " +
                        "values (?, ?, 1) " +
                        "on conflict (order_id, book_id) " +
                        "do update set " +
                        "quantity = EXCLUDED.quantity + 1")) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, bookId);

            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Не удалось добавить книги из заказа с id = %d: %s}", orderId, e.getMessage()), e);
        }
    }

    private int saveOrderData(Order order) throws SQLException {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into orders (" +
                        "user_id, " +
                        "created_at, " +
                        "completed_at, " +
                        "status)" +
                        "values (?, ?, ?, ?)" +
                        "returning id")) {
            stmt.setInt(1, order.getUser().getId());
            LocalDate createdAt = order.getCreatedAt();
            if (createdAt != null) {
                stmt.setDate(2, Date.valueOf(createdAt));
            }
            LocalDate completedAt = order.getCompletedAt();
            if (completedAt != null) {
                stmt.setDate(3, Date.valueOf(completedAt));
            }
            stmt.setString(4, order.getStatus());

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("id");
        } catch (SQLException e) {
            throw new SQLException(String.format("Не удалось добавить заказ: %s", e.getMessage()), e);
        }
    }
}
