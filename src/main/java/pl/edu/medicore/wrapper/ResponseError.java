package pl.edu.medicore.wrapper;

import java.time.OffsetDateTime;

public sealed interface ResponseError permits ValidationError, GeneralError {
    String message();

    OffsetDateTime timestamp();
}
