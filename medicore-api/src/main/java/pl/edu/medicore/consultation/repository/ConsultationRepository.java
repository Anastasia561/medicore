package pl.edu.medicore.consultation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.consultation.model.Consultation;

import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);
}
