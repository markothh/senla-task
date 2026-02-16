package model.service.CSVHandler;

import jakarta.persistence.EntityManager;
import model.config.JPAConfig;
import model.entity.Book;
import model.entity.Request;
import model.repository.BookRepository;
import model.repository.OrderRepository;
import model.repository.RequestRepository;
import model.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class RequestCSVHandler implements ICSVHandler<Request> {
    private static final Logger logger = LogManager.getLogger();
    private static RequestCSVHandler INSTANCE;
    private final BookRepository bookRepository;
    private final RequestRepository requestRepository;

    private static final String EXPORT_SUCCESS_MSG = "Запросы успешно экспортированы в файл '{}'";
    private static final String EXPORT_ERROR_MSG = "Не удалось открыть для записи файл '{}'";
    private static final String ADD_SUCCESS_MSG = "Информация о запросах была получена из файла '{}'";
    private static final String ADD_ERROR_MSG = "Данные запроса не добавлены: {}";
    private static final String FILE_OPEN_ERROR_MSG = "Не удалось открыть для чтения файл '{}'";
    private static final String READ_ERROR_MSG = "Ошибка чтения из файла '{}'";
    private static final String BOOK_NOT_FOUND_ERROR_MSG = "Книга с id = %d не найдена";
    private static final String ASSOCIATION_ERROR = "Не удалось установить соответствия между сущностями: %s";
    private static final String PARSE_ERROR_MSG = "Не удалось сформировать сущность запроса из данных файла. Неверный формат данных: %s";


    private RequestCSVHandler() {
        EntityManager em = JPAConfig.getEntityManager();
        bookRepository = new BookRepository(em);
        requestRepository = new RequestRepository(em);
    }

    public static RequestCSVHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestCSVHandler();
        }
        return INSTANCE;
    }

    @Override
    public void exportToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id;createdAt;book;quantity\n");
            for (Request request : requestRepository.findAll()) {
                writer.write(String.format("%s;%s;%s;%s%n",
                        request.getId(),
                        request.getCreatedAt() != null ? request.getCreatedAt() : "",
                        request.getBook().getId(),
                        request.getQuantity())
                );
            }

            logger.info(EXPORT_SUCCESS_MSG, filePath);
        } catch (IOException e) {
            logger.error(EXPORT_ERROR_MSG, filePath);
        }
    }

    @Override
    public List<Request> importFromCSV(String filePath) {
        List<Request> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    result.add(parseRequest(line));
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

        return result;
    }

    private Book findBook(int bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format(BOOK_NOT_FOUND_ERROR_MSG, bookId)));
    }

    private Request parseRequest(String requestData) {
        String[] args = requestData.split(";");
        try {
             return new Request(
                    Integer.parseInt(args[0]),
                    !args[1].isBlank() ? LocalDate.parse(args[1]) : null,
                    findBook(Integer.parseInt(args[2])),
                    Integer.parseInt(args[3]));
        } catch (NoSuchElementException e) {
            logger.debug(requestData);
            throw new IllegalArgumentException(String.format(ASSOCIATION_ERROR, e.getMessage()));
        } catch (Exception e) {
            logger.debug(requestData);
            throw new IllegalArgumentException(String.format(PARSE_ERROR_MSG, e.getMessage()));
        }
    }
}
