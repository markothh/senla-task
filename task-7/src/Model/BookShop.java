package Model;

import Model.Config.AppConfig;
import Model.Config.AppState;
import Model.Entity.Book;
import Model.Entity.Order;
import Model.Entity.Request;
import Model.Entity.User;
import Model.Enum.OrderStatus;
import Model.Service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

public class BookShop {
    private static BookShop INSTANCE;
    public static Scanner SCANNER = new Scanner(System.in);

    private final RequestService requestService = new RequestService();
    private final OrderService orderService = new OrderService(requestService);
    private final BookService bookService = new BookService();
    private final UserService userService = new UserService();
    private final StatisticsService statistics = new StatisticsService(requestService, orderService, bookService);
    private final AppConfig appConfig = new AppConfig();

    private BookShop() {
        AppState.loadState();
    }

    public static BookShop getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookShop();
        }
        return INSTANCE;
    }

    //----------------------------------------Task 3--------------------------------------------
    public void removeBookFromStock(String bookName) {
        bookService.removeFromStock(bookName);
    }

    public int createOrder(User user, List<String> bookNames) {
        try {
            return orderService.createOrder(user, bookService.formIdListFromNames(bookNames)).getId();
        }
        catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public void cancelOrder(int orderId) {
        orderService.cancelOrder(orderId);
    }

    public void setOrderStatus(int orderId, OrderStatus status) {
        orderService.setOrderStatus(orderId, status);
    }

    public void addBookToStock(String bookName) {
        bookService.addToStock(bookName);
        if (appConfig.getAutoCompleteRequests()) {
            requestService.satisfyAllRequestsByBook(bookService.getBookByName(bookName));
        }
    }

    public int createBookRequest(String bookName) {
        if (bookService.isBookAvailable(bookName)) {
            String errMessage = "Невозможно создать заказ на книгу, которая есть в наличии.";
            Logger.getGlobal().severe(errMessage);
            throw new IllegalArgumentException(errMessage);
        }

        Book bookToRequest = bookService.getBookByName(bookName);
        return requestService.createRequest(bookToRequest).getId();
    }

    //----------------------------------------Task 4--------------------------------------------
    public List<Book> getBooks() {
        return bookService.getBooks();
    }

    public List<Book> getBooks(String sortBy, boolean isReversed) {
        return bookService.getSortedBooks(sortBy, isReversed);
    }

    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    public List<Order> getOrders(String sortBy, boolean isReversed) {
        return orderService.getSortedOrders(sortBy, isReversed);
    }

    public List<Request> getRequests() {
        return requestService.getRequests();
    }

    public List<Request> getRequests(String sortBy, boolean isReversed) {
        return requestService.getSortedRequests(sortBy, isReversed);
    }

    public List<Order> getCompletedOrdersByPeriod(LocalDate startDate, LocalDate endDate) {
        return statistics.getCompletedOrdersByPeriod(startDate, endDate);
    }

    public double getIncomeByPeriod(LocalDate startDate, LocalDate endDate) {
        return statistics.getIncomeByPeriod(startDate, endDate);
    }

    public List<Book> getOverstockedBooks() {
        return statistics.getOverstockedBooks(appConfig.getStaleMonths());
    }

    public String getOrderDetails(int orderId) {
        return orderService.getOrderInfo(orderId);
    }

    public String getBookDescription(String bookName) {
        return bookService.getDescriptionByBookName(bookName);
    }

    //----------------------------------------Task 6--------------------------------------------
    public void exportBooks(String filePath) {
        bookService.exportBooks(filePath);
    }

    public void importBooks(String filePath) {
        bookService.importBooks(filePath);
    }

    public void exportOrders(String filePath) {
        orderService.exportOrders(filePath);
    }

    public void importOrders(String filePath) {
        orderService.importOrders(filePath);
    }

    public void exportRequests(String filePath) {
        requestService.exportRequests(filePath);
    }

    public void importRequests(String filePath) {
        requestService.importRequests(filePath);
    }

    public void exportUsers(String filePath) {
        userService.exportRequests(filePath);
    }

    public void importUsers(String filePath) {
        userService.importRequests(filePath);
    }

    public void login(String username, String password) {
        userService.login(username, password);
    }

    public void logout() {
        userService.logout();
    }

    public List<User> getUsers() {
        return userService.getUsers();
    }
}
