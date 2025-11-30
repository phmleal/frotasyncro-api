package br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities;

import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trucks_trailers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class TruckTrailerCombinationEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "truck_id", nullable = false)
    private TruckEntity truck;

    @ManyToOne
    @JoinColumn(name = "trailer_id", nullable = false)
    private TrailerEntity trailer;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private EmployerEntity employer;

    @Column(name = "initial_mileage", nullable = false)
    private Long initialMileage;

    @Column(name = "final_mileage")
    private Long finalMileage;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public List<UUID> getMachineIds() {
        return List.of(truck.getId(), trailer.getId());
    }
}
