package model.service.CSVHandler;

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
    private final BookRepository bookRepository = BookRepository.getInstance();

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

            logger.info("Книги успешно экспортированы в файл '{}'", filePath);
        } catch (IOException e) {
            logger.error("Не удалось открыть для записи файл '{}'", filePath);
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
                    logger.error("Данные книги не добавлены: {}", e.getMessage());
                }
            }
            logger.info("Информация о книгах была получена из файла '{}'", filePath);
        } catch (FileNotFoundException e) {
            logger.error("Не удалось открыть для чтения файл '{}'", filePath);
        } catch (IOException e) {
            logger.error("Ошибка чтения из файла '{}'", filePath);
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
            throw new IllegalArgumentException(String.format("Не удалось сформировать сущность книги из данных файла. Неверный формат данных: %s", e.getMessage()));
        }

    }
}
