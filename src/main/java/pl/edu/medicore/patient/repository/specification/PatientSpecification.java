package pl.edu.medicore.patient.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.patient.model.Patient;

public class PatientSpecification {
    public static Specification<Patient> search(String query) {
        String pattern = "%" + query.toLowerCase() + "%";

        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern),
                cb.like(cb.lower(root.get("email")), pattern),
                cb.like(cb.lower(root.get("phoneNumber")), pattern)
        );
    }
}
