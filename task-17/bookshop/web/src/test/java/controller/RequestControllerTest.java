package controller;

import model.entity.Book;
import model.entity.Request;
import model.enums.BookStatus;
import model.enums.RequestSortField;
import model.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RequestService requestService;

    private RequestController requestController;
    private Request request;

    @BeforeEach
    void setUp() {
        Book book = new Book(1, "Test Book", "Desc", "Author",
                "Genre", 500.0, BookStatus.OUT_OF_STOCK, 2020, null);
        request = new Request(1, LocalDate.now(), book, 1);
        requestController = new RequestController(requestService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();
    }

    @Test
    void getRequests_noSort_returnsAllRequests() throws Exception {
        when(requestService.getRequests()).thenReturn(List.of(request));

        mockMvc.perform(get("/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getRequests_emptyList_returnsEmpty() throws Exception {
        when(requestService.getRequests()).thenReturn(List.of());

        mockMvc.perform(get("/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getRequests_withSort_returnsSortedRequests() throws Exception {
        when(requestService.getSortedRequests(RequestSortField.QUANTITY, false))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests")
                        .param("sort", "QUANTITY")
                        .param("isReversed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getRequestById_found_returnsRequest() throws Exception {
        when(requestService.getRequestById(1)).thenReturn(request);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getRequestById_notFound_throwsNoSuchElement() {
        when(requestService.getRequestById(99))
                .thenThrow(new NoSuchElementException("Запрос не найден"));

        assertThrows(NoSuchElementException.class,
                () -> requestController.getRequestById(99));
    }

    @Test
    void getRequestByBookId_found_returnsRequest() throws Exception {
        when(requestService.getRequestByBookId(1)).thenReturn(request);

        mockMvc.perform(get("/requests/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.book.id").value(1));
    }

    @Test
    void getRequestByBookId_notFound_throwsNoSuchElement() {
        when(requestService.getRequestByBookId(99))
                .thenThrow(new NoSuchElementException("Запрос не найден"));

        assertThrows(NoSuchElementException.class,
                () -> requestController.getRequestByBookId(99));
    }

    @Test
    void createRequest_success_returnsRequest() throws Exception {
        when(requestService.createRequest(1)).thenReturn(request);

        mockMvc.perform(post("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createRequest_bookAvailable_throwsIllegalArgument() {
        when(requestService.createRequest(1))
                .thenThrow(new IllegalArgumentException("Книга уже есть на складе"));

        assertThrows(IllegalArgumentException.class,
                () -> requestController.createRequest(1));
    }
}
