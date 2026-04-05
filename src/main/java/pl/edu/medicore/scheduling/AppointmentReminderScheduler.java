package pl.edu.medicore.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.edu.medicore.appointment.service.AppointmentService;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {
    private final AppointmentService appointmentService;

    @Scheduled(cron = "0 */10 * * * *", zone = "Europe/Warsaw")
    public void sendUpcomingAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));

        LocalDateTime from = now.plusHours(24).minusMinutes(5);
        LocalDateTime to = now.plusHours(24).plusMinutes(5);

        appointmentService.sendReminderAboutAppointmentsBetween(from, to);
    }
}
