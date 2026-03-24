package model.service.CSVHandler;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

public interface ICSVHandler<T> {
    void exportToCSV(List<T> items, OutputStream os);
    List<T> importFromCSV(File filePath);
}
