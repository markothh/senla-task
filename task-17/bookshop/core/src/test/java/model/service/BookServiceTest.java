package model.service;

import jakarta.persistence.EntityManager;
import model.entity.Book;
import model.enums.BookSortField;
import model.enums.BookStatus;
import model.repository.BookRepository;
import model.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EntityManager em;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() throws Exception {
        book = new Book(1, "Test Book", "Description", "Author",
                "Genre", 500.0, BookStatus.IN_STOCK, 2020, LocalDate.now());
        Field autoCompleteField = BookService.class.getDeclaredField("autoCompleteRequests");
        autoCompleteField.setAccessible(true);
        autoCompleteField.set(bookService, false);
        Field emField = BookService.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(bookService, em);
    }

    @Test
    void getBooks_returnsListOfBooks() {
        List<Book> books = List.of(book);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getBooks();

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getName());
    }

    @Test
    void getBooks_returnsEmptyList() {
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        List<Book> result = bookService.getBooks();

        assertTrue(result.isEmpty());
    }

    @Test
    void getSortedBooks_sortedByName_returnsSortedList() {
        Book book2 = new Book(2, "Another Book", "Desc", "Author",
                "Genre", 300.0, BookStatus.IN_STOCK, 2021, LocalDate.now());
        when(bookRepository.findAll()).thenReturn(List.of(book, book2));

        List<Book> result = bookService.getSortedBooks(BookSortField.NAME, false);

        assertEquals(2, result.size());
        assertEquals("Another Book", result.get(0).getName());
        assertEquals("Test Book", result.get(1).getName());
    }

    @Test
    void getSortedBooks_reversed_returnsReversedList() {
        Book book2 = new Book(2, "Another Book", "Desc", "Author",
                "Genre", 300.0, BookStatus.IN_STOCK, 2021, LocalDate.now());
        when(bookRepository.findAll()).thenReturn(List.of(book, book2));

        List<Book> result = bookService.getSortedBooks(BookSortField.NAME, true);

        assertEquals("Test Book", result.get(0).getName());
        assertEquals("Another Book", result.get(1).getName());
    }

    @Test
    void getSortedBooks_invalidSortField_throwsNoSuchElement() {
        assertThrows(NoSuchElementException.class,
                () -> bookService.getSortedBooks(null, false));
    }

    @Test
    void getBookById_found_returnsBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1);

        assertEquals(1, result.getId());
        assertEquals("Test Book", result.getName());
    }

    @Test
    void getBookById_notFound_throwsNoSuchElement() {
        when(bookRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> bookService.getBookById(99));
    }

    @Test
    void getBookByName_found_returnsBook() {
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));

        Book result = bookService.getBookByName("Test Book");

        assertEquals("Test Book", result.getName());
    }

    @Test
    void getBookByName_notFound_throwsNoSuchElement() {
        when(bookRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> bookService.getBookByName("Unknown"));
    }

    @Test
    void getDescriptionById_found_returnsDescription() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        String result = bookService.getDescriptionById(1);

        assertEquals("Description", result);
    }

    @Test
    void getDescriptionById_notFound_throwsNoSuchElement() {
        when(bookRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> bookService.getDescriptionById(99));
    }

    @Test
    void formBookListFromIds_allFound_returnsBooks() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        List<Book> result = bookService.formBookListFromIds(List.of(1));

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getName());
    }

    @Test
    void formBookListFromIds_someFound_returnsFoundBooks() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.findById(99)).thenReturn(Optional.empty());

        List<Book> result = bookService.formBookListFromIds(List.of(1, 99));

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getName());
    }

    @Test
    void formBookListFromIds_noneFound_throwsNoSuchElement() {
        when(bookRepository.findById(99)).thenReturn(Optional.empty());
        when(bookRepository.findById(100)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> bookService.formBookListFromIds(List.of(99, 100)));
    }

    @Test
    void addToStock_found_returnsAvailableBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book result = bookService.addToStock(1);

        assertTrue(result.isAvailable());
        verify(em).merge(book);
    }

    @Test
    void addToStock_notFound_throwsNoSuchElement() {
        when(bookRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> bookService.addToStock(99));
    }

    @Test
    void removeFromStock_found_returnsBook() {
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));

        Book result = bookService.removeFromStock("Test Book");

        assertEquals("Test Book", result.getName());
        verify(em).merge(book);
    }

    @Test
    void removeFromStock_notFound_throwsNoSuchElement() {
        when(bookRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> bookService.removeFromStock("Unknown"));
    }

    @Test
    void isBookAvailable_available_returnsTrue() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        assertTrue(bookService.isBookAvailable(1));
    }

    @Test
    void isBookAvailable_unavailable_returnsFalse() {
        Book outOfStock = new Book(2, "Out Book", "Desc", "Author",
                "Genre", 300.0, BookStatus.OUT_OF_STOCK, 2021, null);
        when(bookRepository.findById(2)).thenReturn(Optional.of(outOfStock));

        assertFalse(bookService.isBookAvailable(2));
    }

    @Test
    void isBookAvailable_notFound_returnsFalse() {
        when(bookRepository.findById(99)).thenReturn(Optional.empty());

        assertFalse(bookService.isBookAvailable(99));
    }
}
