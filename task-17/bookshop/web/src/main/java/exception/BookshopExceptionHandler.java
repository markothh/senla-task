package exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class BookshopExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResponse handleNoSuchElement(NoSuchElementException e) {
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        return new ErrorResponse("OPERATION_DENIED", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAny(Exception e) {
        Throwable root = e;
        while (root.getCause() != null && root != root.getCause()) {
            root = root.getCause();
        }

        if (root instanceof NoSuchElementException) {
            return new ErrorResponse("NOT_FOUND", root.getMessage());
        } else if (root instanceof IllegalArgumentException) {
            return new ErrorResponse("OPERATION_DENIED", root.getMessage());
        } else {
            return new ErrorResponse("ERROR", root.getMessage());
        }
    }
}
