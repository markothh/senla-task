package model.utils.orm;

import model.entity.Book;
import model.entity.DTO.UserProfile;
import model.entity.Order;
import model.entity.Request;
import model.entity.User;
import model.enums.BookStatus;
import model.enums.OrderStatus;
import model.enums.UserRole;
import model.status.IOrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EntityParser {

    public static Order parseOrder(ResultSet rs) throws IllegalArgumentException {
        try {
            User user = parseUser(rs);

            return new Order(
                    rs.getInt("order_id"),
                    user,
                    new ArrayList<>(),
                    rs.getDate("order_created_at") != null ? rs.getDate("order_created_at").toLocalDate() : null,
                    rs.getDate("order_completed_at") != null ? rs.getDate("order_completed_at").toLocalDate() : null,
                    IOrderStatus.from(OrderStatus.valueOf(rs.getString("order_status")))
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Не удалось извлечь данные заказа. Неверный формат данных");
        }
    }

    public static Book parseBook(ResultSet rs) throws IllegalArgumentException {
        try {
            return new Book(
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("book_description"),
                    rs.getString("book_author"),
                    rs.getString("book_genre"),
                    rs.getDouble("book_price"),
                    BookStatus.valueOf(rs.getString("book_status")),
                    rs.getInt("book_publish_year"),
                    rs.getDate("book_stock_date") != null ? rs.getDate("book_stock_date").toLocalDate() : null
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Не удалось извлечь данные книги. Неверный формат данных");
        }
    }

    public static Request parseRequest(ResultSet rs) throws IllegalArgumentException {
        try {
            return new Request(
                    rs.getInt("request_id"),
                    rs.getDate("request_created_at") != null ? rs.getDate("request_created_at").toLocalDate() : null,
                    parseBook(rs),
                    rs.getInt("request_quantity")
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Не удалось извлечь данные запроса. Неверный формат данных");
        }
    }

    public static User parseUser(ResultSet rs) throws IllegalArgumentException {
        try {
            return new User(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("user_password"),
                    UserRole.valueOf(rs.getString("user_role"))
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Не удалось извлечь данные пользователя. Неверный формат данных");
        }
    }

    public static UserProfile parseUserProfile(ResultSet rs) throws IllegalArgumentException {
        try {
            return new UserProfile(
                    rs.getInt("user_id"),
                    rs.getString("user_name")
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException("Не удалось извлечь данные пользователя. Неверный формат данных");
        }
    }
}
