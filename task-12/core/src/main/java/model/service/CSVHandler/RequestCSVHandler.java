package model.service.CSVHandler;

import model.entity.Book;
import model.entity.Request;
import model.repository.BookRepository;
import model.repository.RequestRepository;
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
    private final BookRepository bookRepository = BookRepository.getInstance();
    private final RequestRepository requestRepository = RequestRepository.getInstance();

    private RequestCSVHandler() { }

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

            logger.info("Запросы успешно экспортированы в файл: \"{}\"", filePath);
        } catch (IOException e) {
            logger.error("Не удалось открыть для записи файл '{}'", filePath);
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
                    logger.error("Данные запроса не добавлены: {}", e.getMessage());
                }
            }
            logger.info("Информация о запросах была получена из файла '{}'", filePath);
        } catch (FileNotFoundException e) {
            logger.error("Не удалось открыть для чтения файл '{}'", filePath);
        } catch (IOException e) {
            logger.error("Ошибка чтения из файла '{}'", filePath);
        }

        return result;
    }

    private Book findBook(int bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Книга с id = %d не найдена", bookId)));
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
            throw new IllegalArgumentException(String.format("Не удалось установить соответствия между сущностями: %s", e.getMessage()));
        } catch (Exception e) {
            logger.debug(requestData);
            throw new IllegalArgumentException(String.format("Не удалось сформировать сущность запроса из данных файла. Неверный формат данных: %s", e.getMessage()));
        }
    }
}
