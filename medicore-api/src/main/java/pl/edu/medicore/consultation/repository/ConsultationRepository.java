package pl.edu.medicore.consultation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.consultation.model.Workday;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByDoctorPublicId(UUID doctorId);

    Optional<Consultation> findByPublicId(UUID publicId);

    @Query("""
                SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
                FROM Consultation c
                WHERE c.doctor.publicId = :doctorId
                AND c.workday = :workday
            """)
    boolean existsByDoctorIdAndWorkday(UUID doctorId, Workday workday);

    @Query("""
                SELECT c
                FROM Consultation c
                WHERE c.doctor.publicId = :doctorId
                AND c.workday = :workday
            """)
    Optional<Consultation> findByDoctorIdAndWorkday(UUID doctorId, Workday workday);

    void deleteByPublicId(UUID publicId);
}
