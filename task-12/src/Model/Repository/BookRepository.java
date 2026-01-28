package Model.Repository;

import Model.Config.DBConnection;
import Model.Entity.Book;
import Model.Service.CSVHandler.CSVHandlers;
import Model.Utils.EntityParser;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class BookRepository implements Serializable, IRepository<Book> {
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
                    books.add(EntityParser.parseBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    @Override
    public Book findById(int bookId) {
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
                    return EntityParser.parseBook(rs);
                }
                else {
                    String errMessage = String.format("Не удалось получить данные книги с id=%s", bookId);
                    Logger.getGlobal().severe(errMessage);
                    throw new RuntimeException(errMessage);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        } catch (SQLException e) {
            String errMessage = String.format("Не удалось добавить книгу '%s'", book.getName());
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
        }
    }

    public Book findByName(String bookName) {
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
                    return EntityParser.parseBook(rs);
                }
                else {
                    String errMessage = String.format("Не удалось получить данные книги с name=%s", bookName);
                    Logger.getGlobal().severe(errMessage);
                    throw new RuntimeException(errMessage);
                }
            }
        } catch (SQLException e) {
            String errMessage = "Не удалось установить соединение с БД";
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
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
        } catch (SQLException e) {
            String errMessage = "Не удалось импортировать книги";
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
        }
    }

    private BookRepository() {}
}
