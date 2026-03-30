package model.service;

import model.entity.Book;
import model.entity.Request;
import model.enums.BookStatus;
import model.enums.RequestSortField;
import model.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private RequestService requestService;

    private Request request;
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book(1, "Test Book", "Desc", "Author",
                "Genre", 500.0, BookStatus.OUT_OF_STOCK, 2020, null);
        request = new Request(1, LocalDate.now(), book, 1);
    }

    @Test
    void getRequests_returnsListOfRequests() {
        when(requestRepository.findAll()).thenReturn(List.of(request));

        List<Request> result = requestService.getRequests();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void getRequests_returnsEmptyList() {
        when(requestRepository.findAll()).thenReturn(new ArrayList<>());

        List<Request> result = requestService.getRequests();

        assertTrue(result.isEmpty());
    }

    @Test
    void getSortedRequests_sortedByQuantity_returnsSortedList() {
        Book book2 = new Book(2, "Another Book", "Desc", "Author",
                "Genre", 300.0, BookStatus.OUT_OF_STOCK, 2021, null);
        Request request2 = new Request(2, LocalDate.now(), book2, 5);
        when(requestRepository.findAll()).thenReturn(List.of(request, request2));

        List<Request> result = requestService.getSortedRequests(RequestSortField.QUANTITY, false);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getQuantity());
        assertEquals(5, result.get(1).getQuantity());
    }

    @Test
    void getSortedRequests_invalidSortField_throwsNoSuchElement() {
        assertThrows(NoSuchElementException.class,
                () -> requestService.getSortedRequests(null, false));
    }

    @Test
    void getRequestById_found_returnsRequest() {
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));

        Request result = requestService.getRequestById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getRequestById_notFound_throwsNoSuchElement() {
        when(requestRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> requestService.getRequestById(99));
    }

    @Test
    void getRequestByBookId_found_returnsRequest() {
        when(requestRepository.findByBookId(1)).thenReturn(Optional.of(request));

        Request result = requestService.getRequestByBookId(1);

        assertNotNull(result);
        assertEquals(1, result.getBook().getId());
    }

    @Test
    void getRequestByBookId_notFound_throwsNoSuchElement() {
        when(requestRepository.findByBookId(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> requestService.getRequestByBookId(99));
    }

    @Test
    void createRequestIfNotAvailable_newRequest_createsRequest() {
        when(requestRepository.findByBookId(1)).thenReturn(Optional.empty());

        Request result = requestService.createRequestIfNotAvailable(book);

        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
        verify(requestRepository, never()).increaseAmount(anyInt());
    }

    @Test
    void createRequestIfNotAvailable_existingRequest_increasesAmount() {
        when(requestRepository.findByBookId(1)).thenReturn(Optional.of(request));

        Request result = requestService.createRequestIfNotAvailable(book);

        assertNotNull(result);
        verify(requestRepository).increaseAmount(1);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequest_bookNotAvailable_returnsRequest() {
        when(bookService.isBookAvailable(1)).thenReturn(false);
        when(bookService.getBookById(1)).thenReturn(book);
        when(requestRepository.findByBookId(1)).thenReturn(Optional.empty());

        Request result = requestService.createRequest(1);

        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequest_bookAvailable_throwsIllegalArgument() {
        when(bookService.isBookAvailable(1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> requestService.createRequest(1));
    }
}
