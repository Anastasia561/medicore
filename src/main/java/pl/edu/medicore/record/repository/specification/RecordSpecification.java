package pl.edu.medicore.record.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.record.model.Record;
import pl.edu.medicore.record.dto.RecordFilterDto;

import java.time.LocalDate;

public class RecordSpecification {

    public static Specification<Record> withFilter(Long userId, Role role, RecordFilterDto filter) {

        Specification<Record> spec = Specification.where(restrictByUser(userId, role));

        if (filter.startDate() != null || filter.endDate() != null) {
            spec = spec.and(dateBetween(filter.startDate(), filter.endDate()));
        }

        if (filter.specialization() != null && !filter.specialization().isBlank()) {
            spec = spec.and(hasDoctorSpecialization(filter.specialization()));
        }

        if (filter.email() != null && !filter.email().isBlank()) {
            spec = spec.and(hasEmailPart(filter.email(), role));
        }

        return spec;
    }

    private static Specification<Record> restrictByUser(Long userId, Role role) {
        return (root, query, cb) -> {
            var appointmentJoin = root.join("appointment");

            if (role == Role.PATIENT) {
                return cb.equal(appointmentJoin.get("patient").get("id"), userId);
            } else if (role == Role.DOCTOR) {
                return cb.equal(appointmentJoin.get("doctor").get("id"), userId);
            }
            throw new IllegalArgumentException("Invalid role: " + role);
        };
    }

    private static Specification<Record> dateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            var appointmentJoin = root.join("appointment");
            if (start != null && end != null) {
                return cb.between(appointmentJoin.get("date"), start.atStartOfDay(), end.atTime(23, 59, 59));
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(appointmentJoin.get("date"), start.atStartOfDay());
            } else if (end != null) {
                return cb.lessThanOrEqualTo(appointmentJoin.get("date"), end.atTime(23, 59, 59));
            } else {
                return null;
            }
        };
    }

    private static Specification<Record> hasDoctorSpecialization(String specialization) {
        return (root, query, cb) -> {
            var appointmentJoin = root.join("appointment");
            return cb.equal(appointmentJoin.get("doctor").get("specialization"), specialization);
        };
    }

    private static Specification<Record> hasEmailPart(String emailPart, Role role) {
        var pattern = "%" + emailPart.toLowerCase() + "%";

        return (root, query, cb) -> {
            var appointmentJoin = root.join("appointment");
            if (role == Role.PATIENT) {
                return cb.like(cb.lower(appointmentJoin.get("doctor").get("email")), pattern);
            } else {
                return cb.like(cb.lower(appointmentJoin.get("patient").get("email")), pattern);
            }
        };
    }
}
