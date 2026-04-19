package pl.edu.medicore.record.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.prescription.model.Prescription;

import java.util.List;

@Entity
@Getter
@Setter
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "diagnosis", length = 100, nullable = false)
    private String diagnosis;
    @Column(name = "summary", nullable = false)
    private String summary;
    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    @OneToMany(mappedBy = "record", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;
}
