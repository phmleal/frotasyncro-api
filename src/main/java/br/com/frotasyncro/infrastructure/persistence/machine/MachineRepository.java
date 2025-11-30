package br.com.frotasyncro.infrastructure.persistence.machine;

import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<MachineEntity, UUID>, JpaSpecificationExecutor<MachineEntity> {
}
