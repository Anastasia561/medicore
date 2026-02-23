package pl.edu.medicore.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.edu.medicore.wrapper.FieldValidationError;
import pl.edu.medicore.wrapper.ResponseWrapper;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleResourceNotFound(EntityNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleEntityExists(EntityExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Object>> handleGenericException(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {

        List<FieldValidationError> validationErrors = ex.getBindingResult().getAllErrors()
                .stream().map(error -> {
                    if (error instanceof FieldError fe) {
                        return new FieldValidationError(fe.getField(), fe.getDefaultMessage());
                    }
                    return new FieldValidationError(error.getObjectName(), error.getDefaultMessage());
                }).toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.withValidationError(HttpStatus.BAD_REQUEST, "Validation failed", validationErrors));
    }

    private ResponseEntity<ResponseWrapper<Object>> buildError(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ResponseWrapper.withError(status, message));
    }
}
