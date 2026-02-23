package pl.edu.medicore.wrapper;

import java.time.OffsetDateTime;

public record GeneralError(String message, OffsetDateTime timestamp) implements ResponseError {
}
