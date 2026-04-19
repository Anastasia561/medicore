package pl.edu.medicore.wrapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class ResponseWrapper<T> {
    private T data;
    private ResponseError error;
    private String status;
    private int statusCode;

    public static <T> ResponseWrapper<T> ok(T data) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>();
        wrapper.setData(data);
        wrapper.setStatus(HttpStatus.OK.name());
        wrapper.setStatusCode(HttpStatus.OK.value());
        return wrapper;
    }

    public static <T> ResponseWrapper<T> withError(HttpStatus status, String message) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>();
        wrapper.setError(new GeneralError(message, OffsetDateTime.now()));
        wrapper.setStatus(status.name());
        wrapper.setStatusCode(status.value());
        return wrapper;
    }

    public static <T> ResponseWrapper<T> withValidationError(HttpStatus status, String message,
                                                             List<FieldValidationError> validationErrors) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>();
        wrapper.setError(new ValidationError(message, OffsetDateTime.now(), validationErrors));
        wrapper.setStatus(status.name());
        wrapper.setStatusCode(status.value());
        return wrapper;
    }

    public static <T> ResponseWrapper<T> withStatus(HttpStatus status, T data) {
        ResponseWrapper<T> wrapper = new ResponseWrapper<>();
        wrapper.setData(data);
        wrapper.setStatus(status.name());
        wrapper.setStatusCode(status.value());
        return wrapper;
    }
}
