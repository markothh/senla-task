package model.service.CSVHandler;

import jakarta.persistence.EntityManager;
import model.config.JPAConfig;
import model.entity.Book;
import model.entity.Order;
import model.entity.User;
import model.enums.OrderStatus;
import model.repository.BookRepository;
import model.repository.OrderRepository;
import model.repository.UserRepository;
import model.status.IOrderStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class OrderCSVHandler implements ICSVHandler<Order> {
    private static final Logger logger = LogManager.getLogger();
    private static OrderCSVHandler INSTANCE;

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private static final String EXPORT_SUCCESS_MSG = "Заказы успешно экспортированы в файл '{}'";
    private static final String EXPORT_ERROR_MSG = "Не удалось открыть для записи файл '{}'";
    private static final String ADD_SUCCESS_MSG = "Информация о заказах была получена из файла '{}'";
    private static final String ADD_ERROR_MSG = "Данные заказа не добавлены: {}";
    private static final String FILE_OPEN_ERROR_MSG = "Не удалось открыть для чтения файл '{}'";
    private static final String READ_ERROR_MSG = "Ошибка чтения из файла '{}'";
    private static final String USER_NOT_FOUND_ERROR_MSG = "Пользователь с id = %d не найден";
    private static final String ASSOCIATION_ERROR = "Не удалось установить соответствия между сущностями: %s";
    private static final String PARSE_ERROR_MSG = "Не удалось сформировать сущность заказа из данных файла. Неверный формат данных: %s";


    private OrderCSVHandler() {
        EntityManager em = JPAConfig.getEntityManager();
        bookRepository = new BookRepository(em);
        userRepository = new UserRepository(em);
        orderRepository = new OrderRepository(em);
    }

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

            logger.info(EXPORT_SUCCESS_MSG, filePath);
        } catch (IOException e) {
            logger.error(EXPORT_ERROR_MSG, filePath);
        }
    }

    @Override
    public List<Order> importFromCSV(String filePath) {
        List<Order> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    result.add(parseOrder(line));
                } catch (IllegalArgumentException e) {
                    logger.error(ADD_ERROR_MSG, e.getMessage());
                }
            }
            logger.info(ADD_SUCCESS_MSG, filePath);
        } catch (FileNotFoundException e) {
            logger.error(FILE_OPEN_ERROR_MSG, filePath);
        } catch (IOException e) {
            logger.error(READ_ERROR_MSG, filePath);
        }
        return result
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Book> findBooks(String strBookIds) {
        return Arrays.stream(strBookIds.split(","))
                .map(Integer::parseInt)
                .map(bookRepository::findById)
                .flatMap(Optional::stream)
                .toList();
    }


    private User findUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format(USER_NOT_FOUND_ERROR_MSG, userId)));
    }

    private Order parseOrder(String orderData) throws IllegalArgumentException {
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
        } catch (NoSuchElementException e) {
            logger.debug(orderData);
            throw new IllegalArgumentException(String.format(ASSOCIATION_ERROR, e.getMessage()));
        } catch (Exception e) {
            logger.debug(orderData);
            throw new IllegalArgumentException(String.format(PARSE_ERROR_MSG, e.getMessage()));
        }
    }
}
