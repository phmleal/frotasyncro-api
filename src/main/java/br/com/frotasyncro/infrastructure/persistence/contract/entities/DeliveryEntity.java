package br.com.frotasyncro.infrastructure.persistence.contract.entities;

import br.com.frotasyncro.domain.contract.delivery.enums.DeliveryStatus;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckTrailerCombinationEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DeliveryEntity extends ContractEntity {

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destiny;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "final_date")
    private LocalDate finalDate;

    @Column(name = "initial_mileage")
    private Long initialMileage;

    @Column(name = "final_mileage")
    private Long finalMileage;

    @Column(name = "delivery_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @ManyToOne
    @JoinColumn(name = "truck_trailer_combination_id", nullable = false)
    private TruckTrailerCombinationEntity truckTrailerCombination;
}
