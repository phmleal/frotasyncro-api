package br.com.frotasyncro.infrastructure.persistence.tire.entities;

import br.com.frotasyncro.domain.tire.enums.TireSide;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tires_positions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class TirePositionEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false, name = "tire_id")
    private TireEntity tire;

    @ManyToOne
    @JoinColumn(nullable = false, name = "machine_id")
    private MachineEntity machine;

    @Column(nullable = false)
    private Boolean inUse;

    @Column(nullable = false)
    private int axle; // 0 - 8

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TireSide side;

}
