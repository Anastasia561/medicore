package pl.edu.medicore.doctor.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;

public class DoctorSpecification {

    public static Specification<Doctor> search(DoctorFilterDto filter) {
        Specification<Doctor> spec = null;
        String query = filter.query();
        Specialization specialization = filter.specialization();

        if (query != null && !query.isBlank()) {
            String pattern = "%" + query.toLowerCase() + "%";
            spec = (root, q, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("firstName")), pattern),
                            cb.like(cb.lower(root.get("lastName")), pattern),
                            cb.like(cb.lower(root.get("email")), pattern)
                    );
        }

        if (specialization != null) {
            Specification<Doctor> specializationSpec = (root, q, cb) ->
                    cb.equal(root.get("specialization"), specialization);
            spec = (spec == null) ? specializationSpec : spec.and(specializationSpec);
        }

        return spec;
    }
}
