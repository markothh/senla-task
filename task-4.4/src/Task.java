import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Task {
    public static void main() {
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
        bookShop.setOrderStatus(orderId, "new");
        //bookShop.setOrderStatus(orderId, "completed");

        System.out.println("\n----------------Добавление книги на склад----------------");
        bookShop.addBookToStock("Фауст");
        bookShop.setOrderStatus(orderId, "completed"); //проверка возможности выполнить заказ

        System.out.println("\n----------------Создание запроса на книгу----------------");
        bookShop.createBookRequest("Моби Дик");
        bookShop.createBookRequest("Над пропастью во ржи");
        bookShop.createBookRequest("Над пропастью во ржи");
        bookShop.createBookRequest("Моби Дик");
        bookShop.createBookRequest("Скотный двор");
        bookShop.createBookRequest("Моби Дик");
        //bookShop.createBookRequest("Моб Дик");
        //bookShop.createBookRequest("Фауст");

        //----------------------------------------Task 4--------------------------------------------
        System.out.println("\n----------------Просмотр списка книг----------------");
        //System.out.printf("Все книги: %s", bookShop.getBooks());
        System.out.printf("%n%nКниги, отсортированные по алфавиту, порядок - обратный: %s",
                bookShop.getBooks("bookName", true));
        //System.out.printf("%n%nКниги, отсортированные по дате публикации, порядок - прямой: %s",
                //bookShop.getBooks("publishDate", false));
        //System.out.printf("%n%nКниги, отсортированные по цене, порядок - обратный: %s",
                //bookShop.getBooks("price", true));
        //System.out.printf("%n%nКниги, отсортированные по наличию на складе, порядок - прямой: %s",
                //bookShop.getBooks("stockAvailability", false));

        System.out.println("\n----------------Просмотр списка заказов----------------");
        //System.out.printf("Все заказы: %s", bookShop.getOrders());
        System.out.printf("%n%nЗаказы, отсортированные по дате выполнения, порядок - обратный: %s",
                bookShop.getOrders("completedAt", true));
        //System.out.printf("%n%nЗаказы, отсортированные по цене, порядок - прямой: %s",
                //bookShop.getOrders("price", false));
        //System.out.printf("%n%nЗаказы, отсортированные по статусу, порядок - прямой: %s",
                //bookShop.getOrders("status", false));

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
}
