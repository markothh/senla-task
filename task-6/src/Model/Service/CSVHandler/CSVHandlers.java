package Model.Service.CSVHandler;

import Model.Entity.Book;
import Model.Entity.Order;
import Model.Entity.Request;
import Model.Entity.User;

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
