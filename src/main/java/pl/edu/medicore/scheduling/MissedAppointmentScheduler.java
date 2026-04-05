package pl.edu.medicore.scheduling;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.appointment.service.AppointmentService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MissedAppointmentScheduler {
    private final AppointmentService appointmentService;

    @Scheduled(cron = "0 26 14 * * *", zone = "Europe/Warsaw")
    @Transactional
    public void markTodayMissedAppointments() {
        LocalDate today = LocalDate.now();

        appointmentService.getAllAppointmentByStatusAndDate(Status.SCHEDULED, today)
                .forEach(a -> {
                    a.setStatus(Status.MISSED);
                });
    }
}
