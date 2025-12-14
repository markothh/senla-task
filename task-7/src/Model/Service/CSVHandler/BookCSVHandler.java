package Model.Service.CSVHandler;

import Model.Entity.Book;
import Model.Enum.BookStatus;
import Model.Repository.BookRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BookCSVHandler implements ICSVHandler<Book> {
    private static BookCSVHandler INSTANCE;
    private final BookRepository bookRepository = BookRepository.getInstance();

    private BookCSVHandler() {}

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

            for (Book book : bookRepository.getBooks()) {
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

            System.out.println("Книги успешно экспортированы в указанный файл.");
            Logger.getGlobal().info(String.format("Книги были экспортированы в файл: \"%s\"", filePath));
        }
        catch (IOException e) {
            Logger.getGlobal().severe("Не удалось открыть для записи указанный файл");
        }
    }

    @Override
    public List<Book> importFromCSV(String filePath) {
        List<Book> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                result.add(parseBook(line));
            }
            System.out.println("Книги были успешно импортированы из указанного файла");
        } catch (FileNotFoundException e) {
            Logger.getGlobal().severe("Не удалось открыть для чтения указанный файл.");
        } catch (IOException e) {
            Logger.getGlobal().severe("Ошибка чтения из указанного файла.");
        }
        Logger.getGlobal().info(String.format("Книги были импортированы из файла: \"%s\"", filePath));
        return result;
    }

    private Book parseBook(String bookData) {
        String[] args = bookData.split(";");
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
    }
}
