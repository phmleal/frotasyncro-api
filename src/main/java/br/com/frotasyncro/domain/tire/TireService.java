package br.com.frotasyncro.domain.tire;

import br.com.frotasyncro.controller.tire.model.SummaryTireResponseDTO;
import br.com.frotasyncro.core.specification.FilterCriteriaSpecification;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.domain.tire.enums.TireSide;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.TireHistoryRepository;
import br.com.frotasyncro.infrastructure.persistence.tire.TirePositionRepository;
import br.com.frotasyncro.infrastructure.persistence.tire.TireRepository;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireHistoryEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TirePositionEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class TireService {

    private final TireRepository tireRepository;
    private final TirePositionRepository tirePositionRepository;
    private final TireHistoryRepository tireHistoryRepository;

    public void saveTire(TireEntity tireEntity) {
        tireRepository.save(tireEntity);
    }

    public TireEntity findById(UUID id) {
        return tireRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<TireEntity> listTirePageableAndFiltered(Pageable pageable, List<FilterCriteria> filterCriteria) {
        return filterCriteria != null && !filterCriteria.isEmpty() ?
                tireRepository.findAll(new FilterCriteriaSpecification<>(filterCriteria), pageable) :
                tireRepository.findAll(pageable);
    }

    public Page<SummaryTireResponseDTO> listSummaryTirePageableAndFiltered(Pageable pageable) {
        return tireRepository.getSummaryTires(pageable);
    }

    public Page<SummaryTireResponseDTO> listAvailableTiresForNewPositions(Pageable pageable) {
        return tireRepository.getAvailableTiresForNewPositions(pageable);
    }

    public void deleteTire(UUID id) {
        tireRepository.deleteById(id);
    }

    public Boolean existsTirePositionInUse(UUID machineId, UUID tireId,
                                           int axle, TireSide side) {
        return tirePositionRepository.existsTirePositionInUse(machineId, tireId, axle, side);
    }


    public void saveTirePosition(TirePositionEntity tirePositionEntity) {
        tirePositionRepository.save(tirePositionEntity);
    }

    public List<TirePositionEntity> findTiresPositionsInUseByMachine(MachineEntity machineEntity) {
        return tirePositionRepository.findByMachineAndInUseOrderByAxleAscSideAsc(machineEntity, TRUE);
    }

    public Optional<TirePositionEntity> findTirePositionByTire(TireEntity tireEntity) {
        return tirePositionRepository.findByTire(tireEntity);
    }

    public TirePositionEntity findTirePositionById(UUID id) {
        return tirePositionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public void inactivateTirePosition(TirePositionEntity tirePosition) {
        tirePosition.setInUse(FALSE);

        saveTirePosition(tirePosition);
    }

    public void saveTireHistory(TireHistoryEntity tireHistoryEntity) {
        tireHistoryRepository.save(tireHistoryEntity);
    }

    public void addMileageToTireByMachineOwner(MachineEntity machineEntity, long mileageToAdd) {
        findTiresPositionsInUseByMachine(machineEntity)
                .forEach(tirePositionEntity -> {
                    TireEntity tire = tirePositionEntity.getTire();
                    tire.setMileage(tire.getMileage() + mileageToAdd);
                    saveTire(tire);
                });
    }

    public Page<SummaryTireResponseDTO> listTiresByEmployerPageable(EmployerEntity employer, PageRequest pageRequest) {
        return tireRepository.findTiresByEmployer(employer, pageRequest);
    }
}
