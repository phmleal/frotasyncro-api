package br.com.frotasyncro.infrastructure.persistence.machine.wheeling;

import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckTrailerCombinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TruckTrailerCombinationRepository extends JpaRepository<TruckTrailerCombinationEntity, UUID>, JpaSpecificationExecutor<TruckTrailerCombinationEntity> {

    @Query("""
            select
            case when count(tt) > 0 then true else false end
            from TruckTrailerCombinationEntity tt
            where (tt.truck.id = :truckId or tt.trailer.id = :trailerId) and tt.finishedAt is null
            """)
    Boolean existsByTruckOrTrailerAndNotFinished(UUID truckId, UUID trailerId);

    @Query("""
            select count(tt)
            from TruckTrailerCombinationEntity tt
            where tt.finishedAt is null
            """)
    long countByNotFinished();

    @Query("""
            select tt
            from TruckTrailerCombinationEntity tt
            where tt.finishedAt is null and tt.employer = :employerEntity
            """)
    Optional<TruckTrailerCombinationEntity> findByEmployerAndNotFinished(EmployerEntity employerEntity);
}
