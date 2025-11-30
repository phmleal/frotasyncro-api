package br.com.frotasyncro.domain.contract.delivery;

import br.com.frotasyncro.infrastructure.persistence.contract.ContractRepository;
import br.com.frotasyncro.infrastructure.persistence.contract.entities.ContractEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    public ContractEntity findContractById(UUID contractId) {
        return contractRepository
                .findById(contractId).orElseThrow(EntityNotFoundException::new);
    }

    public void saveContract(ContractEntity contractEntity) {
        contractRepository.save(contractEntity);
    }

}
