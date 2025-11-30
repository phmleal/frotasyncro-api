package br.com.frotasyncro.infrastructure.persistence.tire;

import br.com.frotasyncro.controller.tire.model.SummaryTireResponseDTO;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TireRepository extends JpaRepository<TireEntity, UUID>, JpaSpecificationExecutor<TireEntity> {

    @Query(value = """
            SELECT new br.com.frotasyncro.controller.tire.model.SummaryTireResponseDTO(
                t.id,
                t.fireCode,
                t.manufacturer,
                t.manufactureYear,
                t.mileage,
                t.tireCondition,
                t.tireStatus,
                case when m is not null then m.licensePlate end
            )
            FROM TireEntity t
            LEFT JOIN TirePositionEntity tp on tp.tire = t
            LEFT JOIN MachineEntity m on tp.machine = m
            """)
    Page<SummaryTireResponseDTO> getSummaryTires(Pageable pageable);

    @Query(value = """
            SELECT new br.com.frotasyncro.controller.tire.model.SummaryTireResponseDTO(
                t.id,
                t.fireCode,
                t.manufacturer,
                t.manufactureYear,
                t.mileage,
                t.tireCondition,
                t.tireStatus,
                case when m is not null then m.licensePlate end
            )
            FROM TireEntity t
            LEFT JOIN TirePositionEntity tp on tp.tire = t AND tp.inUse = true
            LEFT JOIN MachineEntity m on tp.machine = m
            WHERE tp.id IS NULL
            """)
    Page<SummaryTireResponseDTO> getAvailableTiresForNewPositions(Pageable pageable);

    @Query("""
            SELECT new br.com.frotasyncro.controller.tire.model.SummaryTireResponseDTO(
                  tp.tire.id,
                  tp.tire.fireCode,
                  tp.tire.manufacturer,
                  tp.tire.manufactureYear,
                  tp.tire.mileage,
                  tp.tire.tireCondition,
                  tp.tire.tireStatus,
                  tp.machine.licensePlate
              )
              FROM TruckTrailerCombinationEntity tt
              LEFT JOIN TirePositionEntity tp on tp.machine = tt.truck or tp.machine = tt.trailer
              WHERE tt.employer = :employer and tt.finishedAt is null
            """)
    Page<SummaryTireResponseDTO> findTiresByEmployer(EmployerEntity employer,
                                                     Pageable pageable);
}
