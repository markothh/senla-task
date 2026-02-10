package model.service.CSVHandler;

import java.util.List;

public interface ICSVHandler<T> {
    void exportToCSV(String filePath);
    List<T> importFromCSV(String filePath);
}
