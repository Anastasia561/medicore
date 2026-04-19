package pl.edu.medicore.exception;

public class AppointmentCancellationConflictException extends RuntimeException {
    public AppointmentCancellationConflictException(String message) {
        super(message);
    }
}
