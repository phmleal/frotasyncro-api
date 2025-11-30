package br.com.frotasyncro.infrastructure.persistence.contract;

import br.com.frotasyncro.infrastructure.persistence.contract.entities.DeliveryEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryEntity, UUID>, JpaSpecificationExecutor<DeliveryEntity> {

    @Query("select count(d) from DeliveryEntity d where d.finalDate is null")
    long countByFinalDateNull();

    @Query("""
            select coalesce(sum(d.contractValue), 0)
            from DeliveryEntity d
            where year(d.createdAt) = year(current_date)
            and month(d.createdAt) = month(current_date)
            """)
    BigDecimal calculateMonthlyAmountToReceive();

    @Query("""
            select d from DeliveryEntity d
            where d.employer = :employerEntity
            order by d.createdAt desc
            limit 1
            """)
    Optional<DeliveryEntity> findLastByEmployer(EmployerEntity employerEntity);
}
