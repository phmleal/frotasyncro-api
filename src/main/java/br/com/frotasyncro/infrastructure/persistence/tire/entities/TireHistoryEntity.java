package br.com.frotasyncro.infrastructure.persistence.tire.entities;

import br.com.frotasyncro.domain.tire.enums.TireEventType;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tires_histories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class TireHistoryEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false, name = "tire_id")
    private TireEntity tire;

    @Column(nullable = false)
    private Long mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "tire_event_type", nullable = false)
    private TireEventType type;

    @Column(name = "license_plate")
    private String licensePlate;

    private String position;

    private String observation;

    private String workshop;

}
