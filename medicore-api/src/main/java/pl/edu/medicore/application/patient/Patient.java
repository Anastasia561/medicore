package pl.edu.medicore.application.patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.person.Person;

@Entity
@Getter
@Setter
public class Patient extends Person {
    private Double weight;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PregnancyStatus pregnancyStatus;
    private Double height;
}
