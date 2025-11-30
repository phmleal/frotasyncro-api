package br.com.frotasyncro.infrastructure.persistence.employer;

import br.com.frotasyncro.controller.employer.model.SummaryEmployerResponseDTO;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployerRepository extends JpaRepository<EmployerEntity, UUID>, JpaSpecificationExecutor<EmployerEntity> {

    @Query("select e from EmployerEntity e where e.user = :user")
    Optional<EmployerEntity> findByUser(UserEntity user);

    @Query("select count(e) from EmployerEntity e where e.user.status = 'ACTIVE'")
    long countByUserStatusActive();

    @Query("""
            select count(e) from EmployerEntity e 
            where e.user.status = 'ACTIVE' 
            and e.user.id not in (
                select u.id from UserEntity u 
                join u.roles r 
                where lower(r.authority) like '%admin%'
            )
            """)
    long countActiveNonAdminEmployers();

    @Query("""
            select new br.com.frotasyncro.controller.employer.model.SummaryEmployerResponseDTO(
                e.id,
                e.fullName,
                e.commissionPercentage,
                (select t.id from TruckTrailerCombinationEntity t 
                 where t.employer = e and t.finishedAt is null 
                 order by t.createdAt desc limit 1),
                (select concat(t.truck.licensePlate, ' - ', t.trailer.licensePlate) from TruckTrailerCombinationEntity t 
                 where t.employer = e and t.finishedAt is null 
                 order by t.createdAt desc limit 1)
            ) from EmployerEntity e
            where e.user.id not in (
                select u.id from UserEntity u 
                join u.roles r 
                where lower(r.authority) like '%admin%'
            )
            order by e.createdAt desc
            """)
    List<SummaryEmployerResponseDTO> findAllSummary();
}
