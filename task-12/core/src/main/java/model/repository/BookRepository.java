package model.repository;

import model.config.DBConnection;
import model.entity.Book;
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

public final class BookRepository implements Serializable, IRepository<Book> {
    private static final Logger logger = LogManager.getLogger();
    private static BookRepository INSTANCE;

    public static BookRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookRepository();
        }
        return INSTANCE;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (var stmt = DBConnection.getInstance().getConnection().createStatement()) {
            try (var rs = stmt.executeQuery("select " +
                    "id as book_id, " +
                    "name as book_name, " +
                    "description as book_description, " +
                    "author as book_author, " +
                    "genre as book_genre, " +
                    "price as book_price, " +
                    "status as book_status, " +
                    "publish_year as book_publish_year, " +
                    "stock_date as book_stock_date " +
                    "from books")) {
                while (rs.next()) {
                    try {
                        books.add(EntityParser.parseBook(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные книги не удалось извлечь из БД: {}", e.getMessage());
                    }
                }
            }
            logger.info("Список книг успешно получен.");
        } catch (SQLException e) {
            logger.error("Не удалось получить список книг: {}", e.getMessage());
        }
        return books;
    }

    @Override
    public Optional<Book> findById(int bookId) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("select " +
                                "id as book_id, " +
                                "name as book_name, " +
                                "description as book_description, " +
                                "author as book_author, " +
                                "genre as book_genre, " +
                                "price as book_price, " +
                                "status as book_status, " +
                                "publish_year as book_publish_year, " +
                                "stock_date as book_stock_date " +
                                "from books " +
                                "where id = ?")) {
            stmt.setInt(1, bookId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Книга с id = {} получена", bookId);
                    try {
                        return Optional.of(EntityParser.parseBook(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные книги не удалось извлечь из БД: {}", e.getMessage());
                        return Optional.empty();
                    }
                } else {
                    logger.error("Не удалось получить данные книги с id = {}", bookId);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить данные книги с id = {}: {}", bookId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void save(Book book) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into books (" +
                        "name, " +
                        "description, " +
                        "author, " +
                        "genre, " +
                        "price, " +
                        "status, " +
                        "publish_year, " +
                        "stock_date)" +
                        "values (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getDescription());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getGenre());
            stmt.setDouble(5, book.getPrice());
            stmt.setString(6, book.getStatus().toString());
            stmt.setInt(7, book.getPublishYear());
            LocalDate stockDate = book.getStockDate();
            if (stockDate != null) {
                stmt.setDate(8, Date.valueOf(stockDate));
            }

            stmt.execute();
            logger.info("Книга '{}' успешно добавлена", book.getName());
        } catch (SQLException e) {
            logger.error("Не удалось добавить книгу '{}': {}", book.getName(), e.getMessage());
        }
    }

    public Optional<Book> findByName(String bookName) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("select " +
                                "id as book_id, " +
                                "name as book_name, " +
                                "description as book_description, " +
                                "author as book_author, " +
                                "genre as book_genre, " +
                                "price as book_price, " +
                                "status as book_status, " +
                                "publish_year as book_publish_year, " +
                                "stock_date as book_stock_date " +
                                "from books " +
                                "where name = ?")) {
            stmt.setString(1, bookName);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Книга с name = {} успешно получена", bookName);
                    try {
                        return Optional.of(EntityParser.parseBook(rs));
                    } catch (IllegalArgumentException e) {
                        logger.error("Данные книги не удалось извлечь из БД: {}", e.getMessage());
                        return Optional.empty();
                    }
                } else {
                    logger.error("Не удалось получить данные книги с name = {}", bookName);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось получить данные книги с name = {}: {}", bookName, e.getMessage());
            return Optional.empty();
        }
    }

    public void exportToCSV(String filePath) {
        CSVHandlers.books().exportToCSV(filePath);

    }

    public void importFromCSV(String filePath) {
        try (var stmt = DBConnection.getInstance().getConnection()
                .prepareStatement("insert into books (" +
                        "id, " +
                        "name, " +
                        "description, " +
                        "author, " +
                        "genre, " +
                        "price, " +
                        "status, " +
                        "publish_year, " +
                        "stock_date) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "on conflict (id) " +
                        "do update set " +
                        "name = EXCLUDED.name, " +
                        "description = EXCLUDED.description, " +
                        "author = EXCLUDED.author, " +
                        "genre = EXCLUDED.genre, " +
                        "price = EXCLUDED.price, " +
                        "status = EXCLUDED.status, " +
                        "publish_year = EXCLUDED.publish_year, " +
                        "stock_date = EXCLUDED.stock_date")) {

            for (Book book : CSVHandlers.books().importFromCSV(filePath)) {
                stmt.setInt(1, book.getId());
                stmt.setString(2, book.getName());
                stmt.setString(3, book.getDescription());
                stmt.setString(4, book.getAuthor());
                stmt.setString(5, book.getGenre());
                stmt.setDouble(6, book.getPrice());
                stmt.setString(7, book.getStatus().toString());
                stmt.setInt(8, book.getPublishYear());
                LocalDate stockDate = book.getStockDate();
                if (stockDate != null) {
                    stmt.setDate(9, Date.valueOf(stockDate));
                }

                stmt.addBatch();
            }

            stmt.executeBatch();
            logger.info("Книги успешно импортированы из файла '{}'", filePath);
        } catch (SQLException e) {
            logger.error("Не удалось импортировать книги из файла '{}'. Подробнее: {}", filePath, e.getMessage());
        }
    }

    private BookRepository() { }
}
