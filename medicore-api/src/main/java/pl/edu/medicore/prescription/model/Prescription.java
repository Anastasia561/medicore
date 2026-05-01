package pl.edu.medicore.prescription.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.record.model.Record;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;
    @Column(name = "medicine", length = 60, nullable = false)
    private String medicine;
    @Column(name = "dosage", length = 20, nullable = false)
    private String dosage;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "frequency", length = 50, nullable = false)
    private String frequency;
    @ManyToOne
    @JoinColumn(name = "record_id")
    private Record record;

    @PrePersist
    public void prePersist() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }
}