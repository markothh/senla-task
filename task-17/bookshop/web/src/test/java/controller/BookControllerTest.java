package controller;

import model.entity.Book;
import model.enums.BookSortField;
import model.enums.BookStatus;
import model.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    private BookController bookController;
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book(1, "Test Book", "Description", "Author",
                "Genre", 500.0, BookStatus.IN_STOCK, 2020, LocalDate.now());
        bookController = new BookController(bookService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .build();
    }

    @Test
    void getBooks_noSort_returnsAllBooks() throws Exception {
        when(bookService.getBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Book"));
    }

    @Test
    void getBooks_emptyList_returnsEmpty() throws Exception {
        when(bookService.getBooks()).thenReturn(List.of());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getBooks_withSort_returnsSortedBooks() throws Exception {
        when(bookService.getSortedBooks(BookSortField.NAME, false)).thenReturn(List.of(book));

        mockMvc.perform(get("/books")
                        .param("sort", "NAME")
                        .param("isReversed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Book"));
    }

    @Test
    void getBookById_found_returnsBook() throws Exception {
        when(bookService.getBookById(1)).thenReturn(book);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Book"));
    }

    @Test
    void getBookById_notFound_throwsNoSuchElement() {
        when(bookService.getBookById(99))
                .thenThrow(new NoSuchElementException("Книга не найдена"));

        assertThrows(NoSuchElementException.class, () -> bookController.getBookById(99));
    }

    @Test
    void getDescriptionById_found_returnsDescription() throws Exception {
        when(bookService.getDescriptionById(1)).thenReturn("Description");

        mockMvc.perform(get("/books/1/description"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Description"));
    }

    @Test
    void getDescriptionById_notFound_throwsNoSuchElement() {
        when(bookService.getDescriptionById(99))
                .thenThrow(new NoSuchElementException("Book not found"));

        assertThrows(NoSuchElementException.class, () -> bookController.getDescriptionById(99));
    }

    @Test
    void addToStock_success_returnsBook() throws Exception {
        when(bookService.addToStock(1)).thenReturn(book);

        mockMvc.perform(patch("/books/1/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Book"));
    }

    @Test
    void addToStock_notFound_throwsNoSuchElement() {
        when(bookService.addToStock(99))
                .thenThrow(new NoSuchElementException("Книга не найдена"));

        assertThrows(NoSuchElementException.class, () -> bookController.addToStock(99));
    }
}
