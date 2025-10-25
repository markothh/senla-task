import java.util.ArrayList;
import java.util.Arrays;

public class Task {
    public static void main() {
        User user = new User("markothh");
        BookShop bookShop = new BookShop();

        System.out.println("----------------Списание книги со склада----------------");
        bookShop.removeBookFromStock("Фауст");

        System.out.println("\n----------------Создание заказа----------------");
        int orderId = bookShop.createOrder(user.getId(), new ArrayList<>(Arrays.asList(
                "Фауст",
                "Гарри Поттер и философский камень",
                "Мастер и Маргарита"
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
        //bookShop.createBookRequest("Моб Дик");
        //bookShop.createBookRequest("Фауст");
    }
}
