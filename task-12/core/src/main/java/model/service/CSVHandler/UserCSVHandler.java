package model.service.CSVHandler;

import model.entity.User;
import model.enums.UserRole;
import model.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public final class UserCSVHandler implements ICSVHandler<User> {
    private static final Logger logger = LogManager.getLogger();
    private static UserCSVHandler INSTANCE;
    private final UserRepository userRepository = UserRepository.getInstance();

    private UserCSVHandler() { }

    public static UserCSVHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserCSVHandler();
        }
        return INSTANCE;
    }

    @Override
    public void exportToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id;name;password;role\n");
            for (User user : userRepository.findAll()) {
                writer.write(String.format("%s;%s;%s;%s%n",
                        user.getId(),
                        user.getName(),
                        user.getPassword(),
                        user.getRole())
                );
            }

            logger.info("Пользователи успешно экспортированы в файл '{}'", filePath);
        } catch (IOException e) {
            logger.error("Не удалось открыть для записи файл '{}'", filePath);
        }
    }

    @Override
    public List<User> importFromCSV(String filePath) {
        List<User> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    result.add(parseUser(line));
                } catch (IllegalArgumentException e) {
                    logger.error("Данные пользователя не добавлены: {}", e.getMessage());
                }
            }
            logger.info("Информация о пользователях была получена из файла '{}'", filePath);
        } catch (FileNotFoundException e) {
            logger.error("Не удалось открыть для чтения файл '{}'", filePath);
        } catch (IOException e) {
            logger.error("Ошибка чтения из файла '{}'", filePath);
        }

        return result;
    }

    private User parseUser(String userData) throws IllegalArgumentException {
        String[] args = userData.split(";");
        try {
            return new User(
                    Integer.parseInt(args[0]),
                    args[1],
                    args[2],
                    UserRole.valueOf(args[3])
            );
        } catch (Exception e) {
            logger.debug(userData);
            throw new IllegalArgumentException(String.format("Не удалось сформировать сущность пользователя из данных файла. Неверный формат данных: %s", e.getMessage()));
        }
    }
}
