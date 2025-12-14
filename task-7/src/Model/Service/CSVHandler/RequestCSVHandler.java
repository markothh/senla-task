package Model.Service.CSVHandler;

import Model.Entity.Book;
import Model.Entity.Request;
import Model.Repository.BookRepository;
import Model.Repository.RequestRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;

public class RequestCSVHandler implements ICSVHandler<Request> {
    private static RequestCSVHandler INSTANCE;
    private final BookRepository bookRepository = BookRepository.getInstance();
    private final RequestRepository requestRepository = RequestRepository.getInstance();

    private RequestCSVHandler() {}

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
            for (Request request : requestRepository.getRequests()) {
                writer.write(String.format("%s;%s;%s;%s%n",
                        request.getId(),
                        request.getCreatedAt() != null ? request.getCreatedAt() : "",
                        request.getBook().getId(),
                        request.getQuantity())
                );
            }

            System.out.println("Запросы успешно экспортированы в указанный файл.");
            Logger.getGlobal().info(String.format("Запросы были экспортированы в файл: \"%s\"", filePath));
        }
        catch (IOException e) {
            Logger.getGlobal().severe("Не удалось открыть для записи указанный файл");
        }
    }

    @Override
    public List<Request> importFromCSV(String filePath) {
        List<Request> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                result.add(parseRequest(line));
            }

            Logger.getGlobal().info(String.format("Запросы были импортированы из файла: \"%s\"", filePath));
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

    private Book findBook(int bookId) {
        return bookRepository.getBookById(bookId);
    }

    private Request parseRequest(String requestData) {
        String[] args = requestData.split(";");
        try {
            return new Request(
                    Integer.parseInt(args[0]),
                    !args[1].isBlank() ? LocalDate.parse(args[1]) : null,
                    findBook(Integer.parseInt(args[2])),
                    Integer.parseInt(args[3])
            );
        }
        catch (NoSuchElementException e) {
            System.out.printf("Не удалось установить соответствия между сущностями. Запрос №%s не был импортирован.", args[0]);
            return null;
        }
    }
}
