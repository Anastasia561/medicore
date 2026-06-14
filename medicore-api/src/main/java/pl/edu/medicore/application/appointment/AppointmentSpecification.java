package pl.edu.medicore.application.appointment;

import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;

import java.time.LocalDate;

class AppointmentSpecification {

    public static Specification<Appointment> withFilter(long userId, AppointmentFilterDto filter) {

        Specification<Appointment> spec =
                Specification.where(hasUser(userId))
                        .and(hasDateBetween(filter.startDate(), filter.endDate()));

        if (filter.status() != null) {
            spec = spec.and(hasStatus(filter.status()));
        }
        return spec;
    }

    private static Specification<Appointment> hasUser(long userId) {
        return (root, query, cb) ->
                cb.or(
                        cb.equal(root.get("doctor").get("id"), userId),
                        cb.equal(root.get("patient").get("id"), userId)
                );
    }

    private static Specification<Appointment> hasDateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) ->
                cb.between(root.get("date"), start, end);
    }

    private static Specification<Appointment> hasStatus(AppointmentStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
}
