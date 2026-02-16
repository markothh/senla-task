package model.service.CSVHandler;

import jakarta.persistence.EntityManager;
import model.config.JPAConfig;
import model.entity.User;
import model.enums.UserRole;
import model.repository.BookRepository;
import model.repository.OrderRepository;
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
    private final UserRepository userRepository = new UserRepository(JPAConfig.getEntityManager());

    private static final String EXPORT_SUCCESS_MSG = "Пользователи успешно экспортированы в файл '{}'";
    private static final String EXPORT_ERROR_MSG = "Не удалось открыть для записи файл '{}'";
    private static final String ADD_SUCCESS_MSG = "Информация о пользователях была получена из файла '{}'";
    private static final String ADD_ERROR_MSG = "Данные пользователя не добавлены: {}";
    private static final String FILE_OPEN_ERROR_MSG = "Не удалось открыть для чтения файл '{}'";
    private static final String READ_ERROR_MSG = "Ошибка чтения из файла '{}'";
    private static final String PARSE_ERROR_MSG = "Не удалось сформировать сущность пользователя из данных файла. Неверный формат данных: %s";

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

            logger.info(EXPORT_SUCCESS_MSG, filePath);
        } catch (IOException e) {
            logger.error(EXPORT_ERROR_MSG, filePath);
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
            throw new IllegalArgumentException(String.format(PARSE_ERROR_MSG, e.getMessage()));
        }
    }
}
