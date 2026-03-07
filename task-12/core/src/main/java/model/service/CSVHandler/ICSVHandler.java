package model.service.CSVHandler;

import java.util.List;

public interface ICSVHandler<T> {
    void exportToCSV(List<T> items, String filePath);
    List<T> importFromCSV(String filePath);
}
