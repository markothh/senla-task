package model.service.CSVHandler;

import model.entity.User;
import model.enums.UserRole;
import model.repository.UserRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class UserCSVHandler implements ICSVHandler<User> {
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

            Logger.getGlobal().info(String.format("Пользователи были экспортированы в файл: \"%s\"", filePath));
        } catch (IOException e) {
            Logger.getGlobal().severe("Не удалось открыть для записи указанный файл");
        }
    }

    @Override
    public List<User> importFromCSV(String filePath) {
        List<User> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                result.add(parseUser(line));
            }

            Logger.getGlobal().info(String.format("Пользователи были импортированы из файла: \"%s\"", filePath));
        } catch (FileNotFoundException e) {
            Logger.getGlobal().severe("Не удалось открыть для чтения указанный файл.");
        } catch (IOException e) {
            Logger.getGlobal().severe("Ошибка чтения из указанного файла.");
        }
        return result;
    }

    private User parseUser(String userData) {
        String[] args = userData.split(";");
        return new User(
                Integer.parseInt(args[0]),
                args[1],
                args[2],
                UserRole.valueOf(args[3])
        );
    }
}
