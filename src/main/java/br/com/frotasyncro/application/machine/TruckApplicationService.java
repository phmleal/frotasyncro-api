package br.com.frotasyncro.application.machine;

import br.com.frotasyncro.controller.machine.model.CreateTruckRequestDTO;
import br.com.frotasyncro.controller.machine.model.SummaryMachineResponseDTO;
import br.com.frotasyncro.controller.machine.model.TruckResponseDTO;
import br.com.frotasyncro.controller.machine.model.UpdateTruckRequestDTO;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import br.com.frotasyncro.domain.machine.wheeling.TruckService;
import br.com.frotasyncro.domain.machine.wheeling.mapper.TruckMapper;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TruckApplicationService {

    private final TruckService truckService;

    public TruckResponseDTO createTruck(CreateTruckRequestDTO createTruckRequestDTO) {
        TruckEntity truckEntity = TruckMapper.INSTANCE.toEntity(createTruckRequestDTO);

        truckService.saveTruck(truckEntity);

        log.info("Truck {} successfully created", truckEntity.getId());

        return TruckMapper.INSTANCE.toResponseDTO(truckEntity);
    }

    public TruckResponseDTO getTruckDetail(UUID id) {
        TruckEntity truckEntity = truckService.findById(id);

        return TruckMapper.INSTANCE.toResponseDTO(truckEntity);
    }

    public Page<TruckResponseDTO> getTrucks(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        Page<TruckEntity> employerEntityPage = truckService.listTruckPageableAndFiltered(
                filteredAndPageableRequestDTO.getPageRequest(),
                filteredAndPageableRequestDTO.getFilters()
        );

        return employerEntityPage.map(TruckMapper.INSTANCE::toResponseDTO);
    }

    public void updateTruck(UUID id, @Valid UpdateTruckRequestDTO updateTruckRequestDTO) {
        TruckEntity truckEntity = truckService.findById(id);

        TruckMapper.INSTANCE.updateEntity(truckEntity, updateTruckRequestDTO);

        truckService.saveTruck(truckEntity);

        log.info("Truck {} successfully updated", id);
    }

    public void deleteTruck(UUID id) {
        truckService.deleteTruck(id);

        log.info("Truck {} successfully deleted", id);
    }

    public List<SummaryMachineResponseDTO> getAllTrucksSummary() {
        return truckService.findAllSummary().stream()
                .map(summary -> new SummaryMachineResponseDTO(summary.getId(), summary.getLicensePlate()))
                .toList();
    }
}
