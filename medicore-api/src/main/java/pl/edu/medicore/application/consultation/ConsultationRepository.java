package pl.edu.medicore.application.consultation;

import org.springframework.data.jpa.repository.JpaRepository;

interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}
