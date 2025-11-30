package br.com.frotasyncro.infrastructure.persistence.machine.entities;

import br.com.frotasyncro.domain.machine.enums.MachineStatus;
import br.com.frotasyncro.domain.machine.enums.MachineType;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "machines")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class MachineEntity extends BaseEntity {

    @Column(nullable = false, name = "license_plate")
    private String licensePlate;

    @Column(name = "model_year", nullable = false)
    private int modelYear;

    @Column(name = "manufacture_year", nullable = false)
    private int manufactureYear;

    private String brand;

    private String model;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "machine_type")
    private MachineType machineType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "machine_status")
    private MachineStatus machineStatus;

}
