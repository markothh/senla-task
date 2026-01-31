package model.utils;

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
import java.util.logging.Logger;

public class EntityParser {
    public static Order parseOrder(ResultSet rs) {
        try {
            UserProfile user = parseUserProfile(rs);

            return new Order(
                    rs.getInt("order_id"),
                    user,
                    new ArrayList<>(),
                    rs.getDate("order_created_at") != null ? rs.getDate("order_created_at").toLocalDate() : null,
                    rs.getDate("order_completed_at") != null ? rs.getDate("order_completed_at").toLocalDate() : null,
                    IOrderStatus.from(OrderStatus.valueOf(rs.getString("order_status")))
            );
        } catch (SQLException e) {
            String errMessage = "Не удалось извлечь данные заказа.";
            Logger.getGlobal().warning(errMessage);
            throw new RuntimeException(errMessage);
        }
    }

    public static Book parseBook(ResultSet rs) {
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
            String errMessage = "Не удалось извлечь данные книги.";
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
        }
    }

    public static Request parseRequest(ResultSet rs) {
        try {
            return new Request(
                    rs.getInt("request_id"),
                    rs.getDate("request_created_at") != null ? rs.getDate("request_created_at").toLocalDate() : null,
                    parseBook(rs),
                    rs.getInt("request_quantity")
            );
        } catch (SQLException e) {
            String errMessage = "Не удалось извлечь данные запроса.";
            Logger.getGlobal().warning(errMessage);
            throw new RuntimeException(errMessage);
        }
    }

    public static User parseUser(ResultSet rs) {
        try {
            return new User(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("user_password"),
                    UserRole.valueOf(rs.getString("user_role"))
            );
        } catch (SQLException e) {
            String errMessage = "Не удалось извлечь данные пользователя.";
            Logger.getGlobal().warning(errMessage);
            throw new RuntimeException(errMessage);
        }
    }

    public static UserProfile parseUserProfile(ResultSet rs) {
        try {
            return new UserProfile(
                    rs.getInt("user_id"),
                    rs.getString("user_name")
            );
        } catch (SQLException e) {
            String errMessage = "Не удалось извлечь данные пользователя.";
            Logger.getGlobal().warning(errMessage);
            throw new RuntimeException(errMessage);
        }
    }
}
