import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Task {
    public static void main() {
        setupLogs();

        User user = new User("markothh");
        BookShop bookShop = new BookShop();

        //----------------------------------------Task 3--------------------------------------------
        System.out.println("----------------Списание книги со склада----------------");
        bookShop.removeBookFromStock("Фауст");

        System.out.println("\n----------------Создание заказа----------------");
        int orderId = bookShop.createOrder(user, new ArrayList<>(Arrays.asList(
                "Фауст",
                "Гарри Поттер и философский камень",
                "Мастер и Маргарита"
        )));
        bookShop.createOrder(user, new ArrayList<>(Arrays.asList(
                "Гордость и предубеждение",
                "Старик и море"
        )));

        System.out.println("\n----------------Отмена заказа----------------");
        bookShop.cancelOrder(orderId);

        System.out.println("\n----------------Изменение статуса заказа----------------");
        bookShop.setOrderStatus(orderId, OrderStatus.NEW);

        System.out.println("\n----------------Добавление книги на склад----------------");
        bookShop.addBookToStock("Фауст");
        bookShop.setOrderStatus(orderId, OrderStatus.COMPLETED);

        System.out.println("\n----------------Создание запроса на книгу----------------");
        bookShop.createBookRequest("Моби Дик");
        bookShop.createBookRequest("Над пропастью во ржи");
        bookShop.createBookRequest("Над пропастью во ржи");
        bookShop.createBookRequest("Моби Дик");
        bookShop.createBookRequest("Скотный двор");
        bookShop.createBookRequest("Моби Дик");

        //----------------------------------------Task 4--------------------------------------------
        System.out.println("\n----------------Просмотр списка книг----------------");
        System.out.printf("%n%nКниги, отсортированные по алфавиту, порядок - обратный: %s",
                bookShop.getBooks("bookName", true));

        System.out.println("\n----------------Просмотр списка заказов----------------");
        System.out.printf("%n%nЗаказы, отсортированные по дате выполнения, порядок - обратный: %s",
                bookShop.getOrders("completedAt", true));

        System.out.println("\n----------------Просмотр списка запросов----------------");
        System.out.printf("Все запросы: %s", bookShop.getRequests());
        System.out.printf("%n%nЗапросы, отсортированные по алфавиту, порядок - обратный: %s",
                bookShop.getRequests("bookName", true));
        System.out.printf("%n%nЗапросы, отсортированные по количеству, порядок - прямой: %s",
                bookShop.getRequests("quantity", false));

        System.out.println("\n----------------Просмотр списка выполненных заказов за период времени----------------");
        System.out.printf("За последние 3 месяца: %s",
                bookShop.getCompletedOrdersByPeriod(LocalDateTime.now().minusMonths(3), LocalDateTime.now()));
        System.out.printf("%n2 месяца назад - 3 месяца назад: %s",
                bookShop.getCompletedOrdersByPeriod(LocalDateTime.now().minusMonths(3), LocalDateTime.now().minusMonths(2)));

        System.out.println("\n----------------Просмотр заработанных средств за период времени----------------");
        System.out.printf("За последние 3 месяца: %s",
                bookShop.getIncomeByPeriod(LocalDateTime.now().minusMonths(3), LocalDateTime.now()));
        System.out.printf("%n2 месяца назад - 3 месяца назад: %s",
                bookShop.getIncomeByPeriod(LocalDateTime.now().minusMonths(3), LocalDateTime.now().minusMonths(2)));
        System.out.println("\n----------------Просмотр списка «залежавшихся» книг----------------");
        System.out.println(bookShop.getOverstockedBooks());
        System.out.println("\n----------------Просмотр деталей заказа----------------");
        System.out.printf("Детали заказа №%d: %n%s",
                orderId,
                bookShop.getOrderDetails(orderId));
        System.out.println("\n----------------Просмотр описания книги----------------");
        System.out.println(bookShop.getBookDescription("Анна Каренина"));
    }

    private static void setupLogs() {
        try {
            FileHandler fileHandler = new FileHandler("bookshop.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            Logger logger = Logger.getGlobal();
            logger.addHandler(fileHandler);
        }
        catch (IOException e) {
            System.err.println("Ошибка настройки логирования");
        }
    }
}
