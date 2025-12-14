package Model.Config;
import Model.Annotations.ConfigProperty;
import java.util.ArrayList;

public class AppConfig {
    @ConfigProperty(propertyName = "staleMonths")
    private int staleMonths;
    @ConfigProperty(propertyName = "autoCompleteRequests")
    private boolean autoCompleteRequests;
    @ConfigProperty(propertyName = "dataPaths")
    private ArrayList<String> dataPaths = new ArrayList<>();

    public int getStaleMonths() {
        return staleMonths;
    }

    public boolean getAutoCompleteRequests() {
        return autoCompleteRequests;
    }

    public ArrayList<String> getDataFiles() {
        return dataPaths;
    }
}
