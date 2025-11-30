package br.com.frotasyncro.infrastructure.persistence.tire;

import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TireHistoryRepository extends JpaRepository<TireHistoryEntity, UUID>, JpaSpecificationExecutor<TireHistoryEntity> {

}
