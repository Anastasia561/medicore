package pl.edu.medicore.common.exception;

public class AppointmentCancellationConflictException extends RuntimeException {
    public AppointmentCancellationConflictException(String message) {
        super(message);
    }
}
