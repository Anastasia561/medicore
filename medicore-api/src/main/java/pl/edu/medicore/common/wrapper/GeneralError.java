package pl.edu.medicore.common.wrapper;

import java.time.OffsetDateTime;

public record GeneralError(String message, OffsetDateTime timestamp) implements ResponseError {
}
