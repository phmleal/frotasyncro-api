package br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities;

import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "trucks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class TruckEntity extends MachineEntity {

    @Column(nullable = false)
    private int axles;

    @Column(nullable = false)
    private Long mileage;

    @Column(nullable = false)
    private String chassi;

    @Column(nullable = false)
    private String renavam;

    @Column(name = "current_year_ipva")
    private int currentYearIpva;

}
