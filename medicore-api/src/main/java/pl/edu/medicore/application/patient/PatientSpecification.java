package pl.edu.medicore.application.patient;

import org.springframework.data.jpa.domain.Specification;

class PatientSpecification {
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
