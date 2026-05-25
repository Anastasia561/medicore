package pl.edu.medicore.application.consultation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);
}
