package model.service.CSVHandler;

import model.entity.Book;
import model.entity.Order;
import model.entity.Request;
import model.entity.User;

public class CSVHandlers {
    public static ICSVHandler<Book> books() {
        return BookCSVHandler.getInstance();
    }

    public static ICSVHandler<Order> orders() {
        return OrderCSVHandler.getInstance();
    }

    public static ICSVHandler<Request> requests() {
        return RequestCSVHandler.getInstance();
    }

    public static ICSVHandler<User> users() {
        return UserCSVHandler.getInstance();
    }
}
