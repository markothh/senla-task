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

    private static final String REMOVE_FROM_STOCK_INIT_MSG = "Инициирована операция: Удаление книги '{}' со склада";
    private static final String CREATE_ORDER_INIT_MSG = "Инициирована операция: Создание заказа пользователем {}";
    private static final String CANCEL_ORDER_INIT_MSG = "Инициирована операция: Отмена заказа №{}";
    private static final String CHANGE_ORDER_INIT_MSG = "Инициирована операция: Изменение заказа №{}.";
    private static final String ADD_TO_STOCK_INIT_MSG = "Инициирована операция: Добавление книги '{}' на склад.";
    private static final String CREATE_REQUEST_INIT_MSG = "Инициирована операция: Создание запроса на книгу '{}'.";
    private static final String GET_BOOKS_INIT_MSG = "Инициирована операция: Получение списка книг.";
    private static final String GET_SORTED_BOOKS_INIT_MSG = "Инициирована операция: Получение отсортированного списка книг.";
    private static final String GET_ORDERS_INIT_MSG = "Инициирована операция: Получение списка заказов.";
    private static final String GET_SORTED_ORDERS_INIT_MSG = "Инициирована операция: Получение отсортированного списка заказов.";
    private static final String GET_REQUESTS_INIT_MSG = "Инициирована операция: Получение списка запросов.";
    private static final String GET_SORTED_REQUESTS_INIT_MSG = "Инициирована операция: Получение отсортированного списка запросов.";
    private static final String GET_ORDERS_BY_PERIOD_INIT_MSG = "Инициирована операция: Получение списка заказов за период.";
    private static final String GET_INCOME_INIT_MSG = "Инициирована операция: Получение прибыли за период.";
    private static final String GET_OVERSTOCKED_BOOKS_INIT_MSG = "Инициирована операция: Получение списка залежавшихся книг.";
    private static final String GET_ORDER_DETAILS_INIT_MSG = "Инициирована операция: Получение деталей заказа №{}.";
    private static final String GET_DESCRIPTION_INIT_MSG = "Инициирована операция: Получение описание книги '{}'.";
    private static final String BOOK_EXPORT_INIT_MSG = "Инициирована операция: Экспорт списка книг в CSV.";
    private static final String BOOK_IMPORT_INIT_MSG = "Инициирована операция: Импорт списка книг в CSV.";
    private static final String ORDER_EXPORT_INIT_MSG = "Инициирована операция: Экспорт списка заказов в CSV.";
    private static final String ORDER_IMPORT_INIT_MSG = "Инициирована операция: Импорт списка заказов в CSV.";
    private static final String REQUEST_EXPORT_INIT_MSG = "Инициирована операция: Экспорт списка запросов в CSV.";
    private static final String REQUEST_IMPORT_INIT_MSG = "Инициирована операция: Импорт списка запросов в CSV.";
    private static final String USER_EXPORT_INIT_MSG = "Инициирована операция: Экспорт списка пользователей в CSV.";
    private static final String USER_IMPORT_INIT_MSG = "Инициирована операция: Импорт списка пользователей в CSV.";
    private static final String LOGIN_INIT_MSG = "Инициирована операция: Аутентификация пользователя с username={}.";
    private static final String LOGOUT_INIT_MSG = "Инициирована операция: Выход из аккаунта.";
    private static final String GET_USERS_INIT_MSG = "Инициирована операция: Получение списка пользователей.";

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
        logger.info(REMOVE_FROM_STOCK_INIT_MSG, bookName);
        bookService.removeFromStock(bookName);
    }

    public void createOrder(UserProfile user, List<String> bookNames) {
        logger.info(CREATE_ORDER_INIT_MSG, user.getName());
        orderService.createOrder(user, bookNames);
    }

    public void cancelOrder(int orderId) {
        logger.info(CANCEL_ORDER_INIT_MSG, orderId);
        orderService.cancelOrder(orderId);
    }

    public void setOrderStatus(int orderId, OrderStatus status) {
        logger.info(CHANGE_ORDER_INIT_MSG, orderId);
        orderService.setOrderStatus(orderId, status);
    }

    public void addBookToStock(String bookName) {
        logger.info(ADD_TO_STOCK_INIT_MSG, bookName);
        bookService.addToStock(bookName, appConfig.getAutoCompleteRequests());
    }

    public void createBookRequest(String bookName) {
        logger.info(CREATE_REQUEST_INIT_MSG, bookName);
        requestService.createRequest(bookName);
    }

    //----------------------------------------Task 4--------------------------------------------
    public List<Book> getBooks() {
        logger.info(GET_BOOKS_INIT_MSG);
        return bookService.getBooks();
    }

    public List<Book> getBooks(String sortBy, boolean isReversed) {
        logger.info(GET_SORTED_BOOKS_INIT_MSG);
        return bookService.getSortedBooks(sortBy, isReversed);
    }

    public List<Order> getOrders() {
        logger.info(GET_ORDERS_INIT_MSG);
        return orderService.getOrders();
    }

    public List<Order> getOrders(String sortBy, boolean isReversed) {
        logger.info(GET_SORTED_ORDERS_INIT_MSG);
        return orderService.getSortedOrders(sortBy, isReversed);
    }

    public List<Request> getRequests() {
        logger.info(GET_REQUESTS_INIT_MSG);
        return requestService.getRequests();
    }

    public List<Request> getRequests(String sortBy, boolean isReversed) {
        logger.info(GET_SORTED_REQUESTS_INIT_MSG);
        return requestService.getSortedRequests(sortBy, isReversed);
    }

    public List<Order> getCompletedOrdersByPeriod(LocalDate startDate, LocalDate endDate) {
        logger.info(GET_ORDERS_BY_PERIOD_INIT_MSG);
        return statistics.getCompletedOrdersByPeriod(startDate, endDate);
    }

    public double getIncomeByPeriod(LocalDate startDate, LocalDate endDate) {
        logger.info(GET_INCOME_INIT_MSG);
        return statistics.getIncomeByPeriod(startDate, endDate);
    }

    public List<Book> getOverstockedBooks() {
        logger.info(GET_OVERSTOCKED_BOOKS_INIT_MSG);
        return statistics.getOverstockedBooks(appConfig.getStaleMonths());
    }

    public String getOrderDetails(int orderId) {
        logger.info(GET_ORDER_DETAILS_INIT_MSG, orderId);
        return orderService.getOrderInfo(orderId);
    }

    public String getBookDescription(String bookName) {
        logger.info(GET_DESCRIPTION_INIT_MSG, bookName);
        return bookService.getDescriptionByBookName(bookName);
    }

    //----------------------------------------Task 6--------------------------------------------
    public void exportBooks(String filePath) {
        logger.info(BOOK_EXPORT_INIT_MSG);
        bookService.exportBooks(filePath);
    }

    public void importBooks(String filePath) {
        logger.info(BOOK_IMPORT_INIT_MSG);
        bookService.importBooks(filePath);
    }

    public void exportOrders(String filePath) {
        logger.info(ORDER_EXPORT_INIT_MSG);
        orderService.exportOrders(filePath);
    }

    public void importOrders(String filePath) {
        logger.info(ORDER_IMPORT_INIT_MSG);
        orderService.importOrders(filePath);
    }

    public void exportRequests(String filePath) {
        logger.info(REQUEST_EXPORT_INIT_MSG);
        requestService.exportRequests(filePath);
    }

    public void importRequests(String filePath) {
        logger.info(REQUEST_IMPORT_INIT_MSG);
        requestService.importRequests(filePath);
    }

    public void exportUsers(String filePath) {
        logger.info(USER_EXPORT_INIT_MSG);
        userService.exportRequests(filePath);
    }

    public void importUsers(String filePath) {
        logger.info(USER_IMPORT_INIT_MSG);
        userService.importRequests(filePath);
    }

    public void login(String username, String password) {
        logger.info(LOGIN_INIT_MSG, username);
        userService.login(username, password);
    }

    public void logout() {
        logger.info(LOGOUT_INIT_MSG);
        userService.logout();
    }

    public List<User> getUsers() {
        logger.info(GET_USERS_INIT_MSG);
        return userService.getUsers();
    }
}
