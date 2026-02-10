package model.repository;

import model.config.DBConnection;
import model.entity.Request;
import model.service.CSVHandler.CSVHandlers;
import model.utils.EntityParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestRepository implements Serializable, IRepository<Request> {
    private static final Logger logger = LogManager.getLogger();
    private static RequestRepository INSTANCE;

    public static RequestRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestRepository();
        }
        return INSTANCE;
    }

    public Optional<Request> findByBookId(int bookId) {
        try (var stmt = DBConnection.getInstance().getConnection().prepareStatement("select " +
                "r.id as request_id, " +
                "r.created_at as request_created_at, " +
                "r.quantity as request_quantity, " +
                "b.id as book_id, " +
                "b.name as book_name, " +
                "b.description as book_description, " +
                "b.author as book_author, " +
                "b.genre as book_genre, " +
                "b.price as book_price, " +
                "b.status as book_status, " +
                "b.publish_year as book_publish_year, " +
                "b.stock_date as book_stock_date " +
                "from requests r " +
                "join books b on b.id = r.book_id " +
                "where b.id = ?")) {
            stmt.setInt(1, bookId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Успешно получены запросы на книгу с id = {}", bookId);
                    try {
                        return Optional.of(EntityParser.parseRequest(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные запроса не удалось извлечь из БД: {}", e.getMessage());
                        return Optional.empty();
                    }
                } else {
                    logger.error("Запросы на книгу с id = {} не найдены", bookId);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка запроса к БД: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Request> findAll() {
        List<Request> requests = new ArrayList<>();
        try (var stmt = DBConnection.getInstance().getConnection().createStatement()) {
            try (var rs = stmt.executeQuery("select " +
                    "r.id as request_id, " +
                    "r.created_at as request_created_at, " +
                    "r.quantity as request_quantity, " +
                    "b.id as book_id, " +
                    "b.name as book_name, " +
                    "b.description as book_description, " +
                    "b.author as book_author, " +
                    "b.genre as book_genre, " +
                    "b.price as book_price, " +
                    "b.status as book_status, " +
                    "b.publish_year as book_publish_year, " +
                    "b.stock_date as book_stock_date " +
                    "from requests r " +
                    "join books b on b.id = r.book_id ")) {
                while (rs.next()) {
                    try {
                        requests.add(EntityParser.parseRequest(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные запроса не удалось извлечь из БД: {}", e.getMessage());
                    }
                }
                logger.info("Список запросов успешно получен");
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить список пользователей: {}", e.getMessage());
        }
        return requests;
    }

    @Override
    public Optional<Request> findById(int requestId) {
        try (var stmt = DBConnection.getInstance().getConnection().prepareStatement("select " +
                "r.id as request_id, " +
                "r.created_at as request_created_at, " +
                "r.quantity as request_quantity, " +
                "b.id as book_id, " +
                "b.name as book_name, " +
                "b.description as book_description, " +
                "b.author as book_author, " +
                "b.genre as book_genre, " +
                "b.price as book_price, " +
                "b.status as book_status, " +
                "b.publish_year as book_publish_year, " +
                "b.stock_date as book_stock_date " +
                "from requests r " +
                "join books b on b.id = r.book_id " +
                "where r.id = ?")) {
            stmt.setInt(1, requestId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Успешно получены данные запроса с id = {}", requestId);
                    try {
                        return Optional.of(EntityParser.parseRequest(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные запроса не удалось извлечь из БД: {}", e.getMessage());
                        return Optional.empty();
                    }
                } else {
                    logger.error("Не удалось получить данные запроса с id = {}", requestId);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить данные запроса с id = {}: {}", requestId, e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteByBookId(int bookId) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("delete from requests " +
                        "where book_id = ?")) {
            stmt.setInt(1, bookId);

            stmt.execute();
            logger.info("Запрос на книгу с id = {} успешно удален", bookId);
        } catch (SQLException e) {
            logger.error("Не удалось удалить запросы на книгу с id = {}", bookId);
        }
    }

    @Override
    public void save(Request request) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into requests (" +
                        "created_at, " +
                        "book_id, " +
                        "quantity)" +
                        "values (?, ?, ?)")) {
            LocalDate createdAt = request.getCreatedAt();
            if (createdAt != null) {
                stmt.setDate(1, Date.valueOf(createdAt));
            }
            stmt.setInt(2, request.getBook().getId());
            stmt.setInt(3, request.getQuantity());

            stmt.execute();
            logger.info("Запрос на книгу '{}' успешно добавлен", request.getBook().getName());
        } catch (SQLException e) {
            logger.error("Не удалось добавить запрос на книгу '{}': {}", request.getBook().getName(), e.getMessage());
        }
    }

    public void increaseAmount(int requestId) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("update requests set " +
                        "quantity = requests.quantity + 1 " +
                        "where id = ?")) {
            stmt.setInt(1, requestId);

            stmt.execute();
            logger.info("Количество запрашиваемых книг в запросе с id = {} успешно увеличено", requestId);
        } catch (SQLException e) {
            logger.error("Не удалось увеличить количество запрашиваемых книг в запросе с id = {}: {}", requestId, e.getMessage());
        }
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.requests().exportToCSV(filePath);
        logger.info("Запросы успешно экспортированы в файл '{}'", filePath);
    }

    public void importFromCSV(String filePath) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into requests (" +
                        "id, " +
                        "created_at, " +
                        "book_id, " +
                        "quantity)" +
                        "values (?, ?, ?, ?)" +
                        "on conflict (id)" +
                        "do update set" +
                        "created_at = EXCLUDED.created_at, " +
                        "book_id = EXCLUDED.book_id, " +
                        "quantity = EXCLUDED.quantity")) {
            for (Request request : CSVHandlers.requests().importFromCSV(filePath)) {
                stmt.setInt(1, request.getId());
                LocalDate createdAt = request.getCreatedAt();
                if (createdAt != null) {
                    stmt.setDate(2, Date.valueOf(createdAt));
                }
                stmt.setInt(3, request.getBook().getId());
                stmt.setInt(4, request.getQuantity());

                stmt.addBatch();
            }

            stmt.executeBatch();logger.info("Запросы успешно импортированы из файла '{}'", filePath);
        } catch (SQLException e) {
            logger.error("Не удалось импортировать запросы из файла '{}': {}", filePath, e.getMessage());
        }
    }
}
