package br.com.frotasyncro.domain.machine.wheeling;

import br.com.frotasyncro.core.specification.FilterCriteriaSpecification;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.machine.wheeling.exception.InvalidTruckTrailerCombinationException;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.TruckTrailerCombinationRepository;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckTrailerCombinationEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class TruckTrailerCombinationService {

    private final TruckTrailerCombinationRepository truckTrailerCombinationRepository;

    public void checkIfTruckOrTrailerIsInUse(UUID truckId, UUID trailerId) {
        Boolean existsAnyMachineInUse = truckTrailerCombinationRepository
                .existsByTruckOrTrailerAndNotFinished(truckId, trailerId);

        if (TRUE.equals(existsAnyMachineInUse)) {
            throw new InvalidTruckTrailerCombinationException("Truck or Trailer is already in use in another combination.");
        }

    }

    public long countActiveTruckTrailerCombinations() {
        return truckTrailerCombinationRepository.countByNotFinished();
    }

    public void saveTruckTrailerCombination(TruckTrailerCombinationEntity truckTrailerCombinationEntity) {
        truckTrailerCombinationRepository.save(truckTrailerCombinationEntity);
    }

    public TruckTrailerCombinationEntity findById(UUID id) {
        return truckTrailerCombinationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public void finishTruckTrailerCombination(UUID truckTrailerCombinationId, Long finalMileage, LocalDateTime finishedAt) {
        TruckTrailerCombinationEntity truckTrailerCombination = findById(truckTrailerCombinationId);

        truckTrailerCombination.setFinishedAt(finishedAt != null ? finishedAt :
                LocalDateTime.now());
        truckTrailerCombination.setFinalMileage(finalMileage);


        saveTruckTrailerCombination(truckTrailerCombination);
    }

    public Page<TruckTrailerCombinationEntity> listTruckTrailerCombinationPageableAndFiltered(Pageable pageable, List<FilterCriteria> filterCriteria) {
        return filterCriteria != null && !filterCriteria.isEmpty() ?
                truckTrailerCombinationRepository.findAll(new FilterCriteriaSpecification<>(filterCriteria), pageable) :
                truckTrailerCombinationRepository.findAll(pageable);
    }

    public Boolean checkIfIsActive(UUID truckTrailerCombinationId) {
        TruckTrailerCombinationEntity truckTrailerCombination = findById(truckTrailerCombinationId);

        return isNull(truckTrailerCombination.getFinishedAt());
    }

    public Optional<TruckTrailerCombinationEntity> findActiveByEmployer(EmployerEntity employerEntity) {
        return truckTrailerCombinationRepository.findByEmployerAndNotFinished(employerEntity);
    }
}
