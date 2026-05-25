package pl.edu.medicore.application.appointment;

import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.application.doctor.Specialization;

import java.time.LocalDate;
import java.util.UUID;

class AppointmentSpecification {

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

    private static Specification<Appointment> hasUser(UUID userId) {
        return (root, query, cb) ->
                cb.or(
                        cb.equal(root.get("doctor").get("publicId"), userId),
                        cb.equal(root.get("patient").get("publicId"), userId)
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

    private static Specification<Appointment> hasSpecialization(Specialization specialization) {
        return (root, query, cb) ->
                cb.equal(root.get("doctor").get("specialization"), specialization);
    }
}
