package pl.edu.medicore.city.model;

import jakarta.persistence.CascadeType;
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
import pl.edu.medicore.coutry.model.Country;

import java.util.UUID;

@Entity
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;
    @Column(name = "name", length = 30, nullable = false)
    private String name;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "country_id")
    private Country country;

    @PrePersist
    public void prePersist() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

}
