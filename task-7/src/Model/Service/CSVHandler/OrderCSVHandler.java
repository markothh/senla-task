package Model.Service.CSVHandler;

import Model.Entity.Book;
import Model.Entity.DTO.UserProfile;
import Model.Entity.Order;
import Model.Enum.OrderStatus;
import Model.Repository.BookRepository;
import Model.Repository.OrderRepository;
import Model.Repository.UserRepository;
import Model.Status.IOrderStatus;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OrderCSVHandler implements ICSVHandler<Order>{
    private static OrderCSVHandler INSTANCE;

    private final BookRepository bookRepository = BookRepository.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();
    private final OrderRepository orderRepository = OrderRepository.getInstance();

    private OrderCSVHandler() {}

    public static ICSVHandler<Order> getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderCSVHandler();
        }
        return INSTANCE;
    }

    @Override
    public void exportToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id;user;books;created_at;completed_at;status\n");
            for (Order order : orderRepository.findAll()) {
                writer.write(String.format("%s;%s;%s;%s;%s;%s%n",
                        order.getId(),
                        order.getUser().getId(),
                        order.getBooks().stream()
                                .map(Book::getId)
                                .map(Object::toString)
                                .collect(Collectors.joining(",")),
                        order.getCreatedAt() != null ? order.getCreatedAt() : "",
                        order.getCompletedAt() != null ? order.getCompletedAt() : "",
                        order.getStatus())
                );
            }

            Logger.getGlobal().info(String.format("Книги были экспортированы в файл: \"%s\"", filePath));
        }
        catch (IOException e) {
            Logger.getGlobal().severe("Не удалось открыть для записи указанный файл");
        }
    }

    @Override
    public List<Order> importFromCSV(String filePath) {
        List<Order> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                result.add(parseOrder(line));
            }

            Logger.getGlobal().info(String.format("Заказы были импортированы из файла: \"%s\"", filePath));
        } catch (FileNotFoundException e) {
            Logger.getGlobal().severe("Не удалось открыть для чтения указанный файл.");
        } catch (IOException e) {
            Logger.getGlobal().severe("Ошибка чтения из указанного файла.");
        }
        return result
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Book> findBooks(String strBookIds) {
        return Arrays.stream(strBookIds.split(","))
                .map(Integer::parseInt)
                .map(id -> {
                    try {
                        return bookRepository.findById(id);
                    }
                    catch (NoSuchElementException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private UserProfile findUser(int userId) {
        return userRepository.findProfileById(userId);
    }

    private Order parseOrder(String orderData) {
        String[] args = orderData.split(";");
        try {
            return new Order(
                    Integer.parseInt(args[0]),
                    findUser(Integer.parseInt(args[1])),
                    findBooks(args[2]),
                    !args[3].isBlank() ? LocalDate.parse(args[3]) : null,
                    !args[4].isBlank() ? LocalDate.parse(args[4]) : null,
                    IOrderStatus.from(OrderStatus.valueOf(args[5]))
            );
        }
        catch (NoSuchElementException e){
            System.out.printf("Не удалось установить соответствия между сущностями. Заказ №%s не был импортирован.", args[0]);
            return null;
        }
    }
}
