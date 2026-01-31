package Model.Service.CSVHandler;

import java.util.List;

public interface ICSVHandler<T> {
    public void exportToCSV(String filePath);
    public List<T> importFromCSV(String filePath);
}
