package Model.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class AppConfig {
    private final int staleMonths;
    private final boolean autoCompleteRequests;
    private final HashMap<String, String> dataPaths = new HashMap<>();

    public AppConfig(String configFileName) {
        Properties props = new Properties();
        try (InputStream config = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            props.load(config);
        }
        catch (IOException e) {
            String errMessage = "Конфигурация не была применена!";
            Logger.getGlobal().severe(errMessage);
            throw new RuntimeException(errMessage);
        }

        staleMonths = Integer.parseInt(props.getProperty("staleMonths"));
        autoCompleteRequests = Boolean.parseBoolean(props.getProperty("autoCompleteRequests"));

        dataPaths.put("users", props.getProperty("usersData"));
        dataPaths.put("books", props.getProperty("booksData"));
        dataPaths.put("orders", props.getProperty("ordersData"));
        dataPaths.put("requests", props.getProperty("requestsData"));
        dataPaths.put("userContext", props.getProperty("userContextData"));
    }

    public int getStaleMonths() {
        return staleMonths;
    }

    public boolean getAutoCompleteRequests() {
        return autoCompleteRequests;
    }

    public HashMap<String, String> getDataFiles() {
        return dataPaths;
    }
}
