package br.com.frotasyncro.infrastructure.persistence.contract;

import br.com.frotasyncro.infrastructure.persistence.contract.entities.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, UUID>, JpaSpecificationExecutor<ContractEntity> {

}
