package pl.edu.medicore.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentCreateDto(
        @NotNull(message = "Doctor ID must not be null")
        Long doctorId,

        @NotNull(message = "Date must not be null")
        @FutureOrPresent(message = "Appointment date must be today or in the future")
        LocalDate date,

        @NotNull(message = "Time must not be null")
        LocalTime time
) {
}
