package br.com.frotasyncro.infrastructure.persistence.machine.wheeling;

import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TrailerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrailerRepository extends JpaRepository<TrailerEntity, UUID>, JpaSpecificationExecutor<TrailerEntity> {

    @Query("""
            SELECT t FROM TrailerEntity t
            WHERE t.id NOT IN (
                SELECT c.trailer.id FROM TruckTrailerCombinationEntity c
                WHERE c.finishedAt IS NULL
            )
            """)
    List<TrailerEntity> findAvailableTrailersToCoupling();

    @Query("""
            SELECT t.id as id, t.licensePlate as licensePlate
            FROM TrailerEntity t
            """)
    List<TrailerSummary> findAllSummary();

    interface TrailerSummary {
        UUID getId();

        String getLicensePlate();
    }
}
