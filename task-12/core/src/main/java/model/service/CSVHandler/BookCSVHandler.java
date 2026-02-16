package model.service.CSVHandler;

import model.config.JPAConfig;
import model.entity.Book;
import model.enums.BookStatus;
import model.repository.BookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class BookCSVHandler implements ICSVHandler<Book> {
    private static final Logger logger = LogManager.getLogger();
    private static BookCSVHandler INSTANCE;
    private final BookRepository bookRepository = new BookRepository(JPAConfig.getEntityManager());

    private static final String EXPORT_SUCCESS_MSG = "Книги успешно экспортированы в файл '{}'";
    private static final String EXPORT_ERROR_MSG = "Не удалось открыть для записи файл '{}'";
    private static final String ADD_SUCCESS_MSG = "Информация о книгах была получена из файла '{}'";
    private static final String ADD_ERROR_MSG = "Данные книги не добавлены: {}";
    private static final String FILE_OPEN_ERROR_MSG = "Не удалось открыть для чтения файл '{}'";
    private static final String READ_ERROR_MSG = "Ошибка чтения из файла '{}'";
    private static final String PARSE_ERROR_MSG = "Не удалось сформировать сущность книги из данных файла. Неверный формат данных: %s";

    private BookCSVHandler() { }

    public static BookCSVHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookCSVHandler();
        }
        return INSTANCE;
    }

    @Override
    public void exportToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id;name;description;author;genre;price;status;publishYear;stockDate\n");

            for (Book book : bookRepository.findAll()) {
                writer.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s%n",
                        book.getId(),
                        book.getName(),
                        book.getDescription(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPrice(),
                        book.getStatus(),
                        book.getPublishYear(),
                        book.getStockDate() != null ? book.getStockDate() : "")
                );
            }

            logger.info(EXPORT_SUCCESS_MSG, filePath);
        } catch (IOException e) {
            logger.error(EXPORT_ERROR_MSG, filePath);
        }
    }

    @Override
    public List<Book> importFromCSV(String filePath) {
        List<Book> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    result.add(parseBook(line));
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

    private Book parseBook(String bookData) throws IllegalArgumentException {
        String[] args = bookData.split(";");
        try {
            return new Book(
                    Integer.parseInt(args[0]),
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    Double.parseDouble(args[5]),
                    BookStatus.valueOf(args[6]),
                    Integer.parseInt(args[7]),
                    args.length > 8 && !args[8].isBlank() ? LocalDate.parse(args[8]) : null
            );
        } catch (Exception e) {
            logger.debug(bookData);
            throw new IllegalArgumentException(String.format(PARSE_ERROR_MSG, e.getMessage()));
        }

    }
}
