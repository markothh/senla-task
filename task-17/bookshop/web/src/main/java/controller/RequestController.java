package controller;

import model.entity.DTO.RequestDTO;
import model.enums.RequestSortField;
import model.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<RequestDTO> getRequests(
            @RequestParam(value = "sort", required = false) RequestSortField field,
            @RequestParam(value = "isReversed", required = false) boolean isReversed
    ) {
        if (field != null) {
            return requestService.getSortedRequests(field, isReversed).stream()
                    .map(RequestDTO::new)
                    .toList();
        } else {
            return requestService.getRequests().stream()
                    .map(RequestDTO::new)
                    .toList();
        }
    }

    @GetMapping("/{id}")
    public RequestDTO getRequestById(@PathVariable("id") int id) {
        return new RequestDTO(requestService.getRequestById(id));
    }

    @GetMapping("/book/{id}")
    public RequestDTO getRequestByBookId(@PathVariable("id") int bookId) {
        return new RequestDTO(requestService.getRequestByBookId(bookId));
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<RequestDTO> createRequest(@PathVariable("bookId") int bookId) throws IllegalArgumentException {
        return ResponseEntity.ok(new RequestDTO(requestService.createRequest(bookId)));
    }
}
