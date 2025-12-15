package Model.Config;
import Model.Annotations.ConfigProperty;
public class AppConfig {
    private static AppConfig INSTANCE;
    @ConfigProperty(propertyName = "staleMonths")
    private int staleMonths;
    @ConfigProperty(propertyName = "autoCompleteRequests")
    private boolean autoCompleteRequests;

    public int getStaleMonths() {
        return staleMonths;
    }

    public boolean getAutoCompleteRequests() {
        return autoCompleteRequests;
    }

    public static AppConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppConfig();
        }
        return INSTANCE;
    }

    private AppConfig() {}
}
