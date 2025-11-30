package br.com.frotasyncro.domain.machine.wheeling;

import br.com.frotasyncro.core.specification.FilterCriteriaSpecification;
import br.com.frotasyncro.core.specification.model.FilterCriteria;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.TruckRepository;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;

    public void saveTruck(TruckEntity truckEntity) {
        truckRepository.save(truckEntity);
    }

    public TruckEntity findById(UUID id) {
        return truckRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public Page<TruckEntity> listTruckPageableAndFiltered(Pageable pageable, List<FilterCriteria> filterCriteria) {
        return filterCriteria != null && !filterCriteria.isEmpty() ?
                truckRepository.findAll(new FilterCriteriaSpecification<>(filterCriteria), pageable) :
                truckRepository.findAll(pageable);
    }

    public void deleteTruck(UUID id) {
        truckRepository.deleteById(id);
    }

    public List<TruckEntity> findAvailableTrucksToCoupling() {
        return truckRepository.findAvailableTrucksToCoupling();
    }

    public List<TruckRepository.TruckSummary> findAllSummary() {
        return truckRepository.findAllSummary();
    }

    public void addMileageToTruck(TruckEntity truck, long totalMileageTraveled) {
        truck.setMileage(truck.getMileage() + totalMileageTraveled);

        truckRepository.save(truck);
    }
}
