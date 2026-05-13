package pl.edu.medicore.doctor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.person.model.Person;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Doctor extends Person {
    @Column(name = "experience", nullable = false)
    private Integer experience;
    @Column(name = "employment_date", nullable = false)
    private LocalDate employmentDate;
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
    @OneToMany(mappedBy = "doctor", fetch = FetchType.EAGER)
    private Set<Consultation> consultations = new HashSet<>();
}
