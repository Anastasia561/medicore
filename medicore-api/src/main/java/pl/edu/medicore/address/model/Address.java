package pl.edu.medicore.address.model;

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
import pl.edu.medicore.city.model.City;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;
    @Column(name = "street", length = 40, nullable = false)
    private String street;
    @Column(name = "number", nullable = false)
    private Integer number;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "city_id")
    private City city;

    @PrePersist
    public void prePersist() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }
}
