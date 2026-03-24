package controller;

import jakarta.servlet.http.HttpServletResponse;
import model.entity.DTO.BookDetailsDTO;
import model.enums.BookSortField;
import model.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDetailsDTO> getBooks(
            @RequestParam(value = "sort", required = false) BookSortField field,
            @RequestParam(value = "isReversed", required = false) boolean isReversed
    ) {
        if (field != null) {
            return bookService.getSortedBooks(field, isReversed).stream()
                    .map(BookDetailsDTO::new)
                    .toList();
        } else {
            return bookService.getBooks().stream()
                    .map(BookDetailsDTO::new)
                    .toList();
        }
    }

    @GetMapping("/{id}")
    public BookDetailsDTO getBookById(@PathVariable("id") int id) {
        return new BookDetailsDTO(bookService.getBookById(id));
    }

    @GetMapping("/{id}/description")
    public String getDescriptionById(@PathVariable("id") int id) {
        return bookService.getDescriptionById(id);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<BookDetailsDTO> addToStock(@PathVariable("id") int id) {
        return ResponseEntity.ok(new BookDetailsDTO(bookService.addToStock(id)));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload-", ".csv");
        file.transferTo(tempFile);
        bookService.importBooks(tempFile);
        tempFile.delete();
        return ResponseEntity.ok("CSV импортирован успешно");
    }

    @GetMapping("/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"books.csv\"");
        bookService.exportBooks(response.getOutputStream());
    }
}
