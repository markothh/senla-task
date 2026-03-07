package model.service.CSVHandler;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.entity.Book;
import model.entity.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RequestCSVHandler implements ICSVHandler<Request> {
    private static final Logger logger = LogManager.getLogger();

    @PersistenceContext
    private EntityManager em;

    private static final String EXPORT_SUCCESS_MSG = "Запросы успешно экспортированы в файл '{}'";
    private static final String EXPORT_ERROR_MSG = "Не удалось открыть для записи файл '{}'";
    private static final String ADD_SUCCESS_MSG = "Информация о запросах была получена из файла '{}'";
    private static final String ADD_ERROR_MSG = "Данные запроса не добавлены: {}";
    private static final String FILE_OPEN_ERROR_MSG = "Не удалось открыть для чтения файл '{}'";
    private static final String READ_ERROR_MSG = "Ошибка чтения из файла '{}'";
    private static final String ASSOCIATION_ERROR = "Не удалось установить соответствия между сущностями: %s";
    private static final String PARSE_ERROR_MSG = "Не удалось сформировать сущность запроса из данных файла. Неверный формат данных: %s";

    @Override
    public void exportToCSV(List<Request> requests, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id;createdAt;book;quantity\n");
            for (Request request : requests) {
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

    private Request parseRequest(String requestData) {
        String[] args = requestData.split(";");
        try {
             return new Request(
                    Integer.parseInt(args[0]),
                    !args[1].isBlank() ? LocalDate.parse(args[1]) : null,
                    em.getReference(Book.class, args[2]),
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
