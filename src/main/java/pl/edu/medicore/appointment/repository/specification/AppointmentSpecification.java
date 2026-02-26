package pl.edu.medicore.appointment.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;

import java.time.LocalDate;

public class AppointmentSpecification {

    public static Specification<Appointment> withFilter(AppointmentFilterDto filter) {

        Specification<Appointment> spec =
                Specification.where(hasUser(filter.userId()))
                        .and(hasDateBetween(filter.startDate(), filter.endDate()));

        if (filter.status() != null) {
            spec = spec.and(hasStatus(filter.status()));
        }

        if (filter.specialization() != null) {
            spec = spec.and(hasSpecialization(filter.specialization()));
        }

        return spec;
    }

    private static Specification<Appointment> hasUser(Long userId) {
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

    private static Specification<Appointment> hasStatus(Status status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    private static Specification<Appointment> hasSpecialization(String specialization) {
        return (root, query, cb) ->
                cb.equal(root.get("doctor").get("specialization"), specialization);
    }
}
