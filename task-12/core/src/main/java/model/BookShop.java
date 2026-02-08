package model;

import model.annotations.Inject;
import model.config.AppConfig;
import model.config.AppState;
import model.config.JPAConfig;
import model.entity.Book;
import model.entity.DTO.UserProfile;
import model.entity.Order;
import model.entity.Request;
import model.entity.User;
import model.enums.OrderStatus;
import model.service.BookService;
import model.service.OrderService;
import model.service.RequestService;
import model.service.StatisticsService;
import model.service.UserService;
import model.utils.di.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public final class BookShop {
    private static final Logger logger = LogManager.getLogger();
    private static BookShop INSTANCE;
    public static Scanner SCANNER = new Scanner(System.in);
    private final RequestService requestService = new RequestService(JPAConfig.getEntityManager());
    @Inject
    private OrderService orderService;
    private final BookService bookService = new BookService(JPAConfig.getEntityManager());
    @Inject
    private UserService userService;
    @Inject
    private StatisticsService statistics;
    @Inject
    private AppConfig appConfig;

    private BookShop() {
        ConfigLoader.configure(new AppState());
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
        logger.info("Инициирована операция: Удаление книги '{}' со склада", bookName);
        bookService.removeFromStock(bookName);
    }

    public void createOrder(UserProfile user, List<String> bookNames) {
        logger.info("Инициирована операция: Создание заказа пользователем {}", user.getName());
        orderService.createOrder(user, bookNames);
    }

    public void cancelOrder(int orderId) {
        logger.info("Инициирована операция: Отмена заказа №{}", orderId);
        orderService.cancelOrder(orderId);
    }

    public void setOrderStatus(int orderId, OrderStatus status) {
        logger.info("Инициирована операция: Изменение заказа №{}.", orderId);
        orderService.setOrderStatus(orderId, status);
    }

    public void addBookToStock(String bookName) {
        logger.info("Инициирована операция: Добавление книги '{}' на склад.", bookName);
        bookService.addToStock(bookName, appConfig.getAutoCompleteRequests());
    }

    public void createBookRequest(String bookName) {
        logger.info("Инициирована операция: Создание запроса на книгу '{}'.", bookName);
        requestService.createRequest(bookName);
    }

    //----------------------------------------Task 4--------------------------------------------
    public List<Book> getBooks() {
        logger.info("Инициирована операция: Получение списка книг.");
        return bookService.getBooks();
    }

    public List<Book> getBooks(String sortBy, boolean isReversed) {
        logger.info("Инициирована операция: Получение отсортированного списка книг.");
        return bookService.getSortedBooks(sortBy, isReversed);
    }

    public List<Order> getOrders() {
        logger.info("Инициирована операция: Получение списка заказов.");
        return orderService.getOrders();
    }

    public List<Order> getOrders(String sortBy, boolean isReversed) {
        logger.info("Инициирована операция: Получение отсортированного списка заказов.");
        return orderService.getSortedOrders(sortBy, isReversed);
    }

    public List<Request> getRequests() {
        logger.info("Инициирована операция: Получение списка запросов.");
        return requestService.getRequests();
    }

    public List<Request> getRequests(String sortBy, boolean isReversed) {
        logger.info("Инициирована операция: Получение отсортированного списка запросов.");
        return requestService.getSortedRequests(sortBy, isReversed);
    }

    public List<Order> getCompletedOrdersByPeriod(LocalDate startDate, LocalDate endDate) {
        logger.info("Инициирована операция: Получение списка заказов за период.");
        return statistics.getCompletedOrdersByPeriod(startDate, endDate);
    }

    public double getIncomeByPeriod(LocalDate startDate, LocalDate endDate) {
        logger.info("Инициирована операция: Получение прибыли за период.");
        return statistics.getIncomeByPeriod(startDate, endDate);
    }

    public List<Book> getOverstockedBooks() {
        logger.info("Инициирована операция: Получение списка залежавшихся книг.");
        return statistics.getOverstockedBooks(appConfig.getStaleMonths());
    }

    public String getOrderDetails(int orderId) {
        logger.info("Инициирована операция: Получение деталей заказа №{}.", orderId);
        return orderService.getOrderInfo(orderId);
    }

    public String getBookDescription(String bookName) {
        logger.info("Инициирована операция: Получение описание книги '{}'.", bookName);
        return bookService.getDescriptionByBookName(bookName);
    }

    //----------------------------------------Task 6--------------------------------------------
    public void exportBooks(String filePath) {
        logger.info("Инициирована операция: Экспорт списка книг в CSV.");
        bookService.exportBooks(filePath);
    }

    public void importBooks(String filePath) {
        logger.info("Инициирована операция: Импорт списка книг из CSV.");
        bookService.importBooks(filePath);
    }

    public void exportOrders(String filePath) {
        logger.info("Инициирована операция: Экспорт списка заказов в CSV.");
        orderService.exportOrders(filePath);
    }

    public void importOrders(String filePath) {
        logger.info("Инициирована операция: Импорт списка заказов из CSV.");
        orderService.importOrders(filePath);
    }

    public void exportRequests(String filePath) {
        logger.info("Инициирована операция: Экспорт списка запросов в CSV.");
        requestService.exportRequests(filePath);
    }

    public void importRequests(String filePath) {
        logger.info("Инициирована операция: Импорт списка запросов из CSV.");
        requestService.importRequests(filePath);
    }

    public void exportUsers(String filePath) {
        logger.info("Инициирована операция: Экспорт списка пользователей в CSV.");
        userService.exportRequests(filePath);
    }

    public void importUsers(String filePath) {
        logger.info("Инициирована операция: Импорт списка пользователей из CSV.");
        userService.importRequests(filePath);
    }

    public void login(String username, String password) {
        logger.info("Инициирована операция: Аутентификация пользователя с username={}.", username);
        userService.login(username, password);
    }

    public void logout() {
        logger.info("Инициирована операция: Выход из аккаунта.");
        userService.logout();
    }

    public List<User> getUsers() {
        logger.info("Инициирована операция: Получение списка пользователей.");
        return userService.getUsers();
    }
}
