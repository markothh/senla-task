package model.service.CSVHandler;

import model.entity.Book;
import model.entity.Request;
import model.repository.BookRepository;
import model.repository.RequestRepository;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public final class RequestCSVHandler implements ICSVHandler<Request> {
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

            System.out.println("Запросы успешно экспортированы в указанный файл.");
            Logger.getGlobal().info(String.format("Запросы были экспортированы в файл: \"%s\"", filePath));
        } catch (IOException e) {
            Logger.getGlobal().warning("Не удалось открыть для записи указанный файл");
        }
    }

    @Override
    public List<Request> importFromCSV(String filePath) {
        List<Request> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                Request request = parseRequest(line).orElse(null);
                if (request != null) {
                    result.add(request);
                } else {
                    Logger.getGlobal().warning("Запрос не был импортирован: ошибка обработки");
                }
            }

            Logger.getGlobal().info(String.format("Запросы были импортированы из файла: \"%s\"", filePath));
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("Не удалось открыть для чтения указанный файл.");
        } catch (IOException e) {
            Logger.getGlobal().warning("Ошибка чтения из указанного файла.");
        }
        return result
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private Optional<Book> findBook(int bookId) {
        return bookRepository.findById(bookId);
    }

    private Optional<Request> parseRequest(String requestData) {
        String[] args = requestData.split(";");
        try {
            return Optional.of(new Request(
                    Integer.parseInt(args[0]),
                    !args[1].isBlank() ? LocalDate.parse(args[1]) : null,
                    findBook(Integer.parseInt(args[2])).orElseThrow(NoSuchElementException::new),
                    Integer.parseInt(args[3]))
            );
        } catch (NoSuchElementException e) {
            System.out.printf("Запрос №%s: Не удалось установить соответствия между сущностями.", args[0]);
            return Optional.empty();
        }
    }
}
