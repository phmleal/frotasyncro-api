package br.com.frotasyncro.application.machine;

import br.com.frotasyncro.controller.machine.model.CreateTrailerRequestDTO;
import br.com.frotasyncro.controller.machine.model.SummaryMachineResponseDTO;
import br.com.frotasyncro.controller.machine.model.TrailerResponseDTO;
import br.com.frotasyncro.controller.machine.model.UpdateTrailerRequestDTO;
import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import br.com.frotasyncro.domain.machine.wheeling.TrailerService;
import br.com.frotasyncro.domain.machine.wheeling.mapper.TrailerMapper;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TrailerEntity;
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
public class TrailerApplicationService {

    private final TrailerService trailerService;

    public TrailerResponseDTO createTrailer(CreateTrailerRequestDTO createTrailerRequestDTO) {
        TrailerEntity trailerEntity = TrailerMapper.INSTANCE.toEntity(createTrailerRequestDTO);

        trailerService.saveTrailer(trailerEntity);

        log.info("Trailer {} successfully created", trailerEntity.getId());

        return TrailerMapper.INSTANCE.toResponseDTO(trailerEntity);
    }

    public TrailerResponseDTO getTrailerDetail(UUID id) {
        TrailerEntity trailerEntity = trailerService.findById(id);

        return TrailerMapper.INSTANCE.toResponseDTO(trailerEntity);
    }

    public Page<TrailerResponseDTO> getTrailers(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        Page<TrailerEntity> employerEntityPage = trailerService.listEmployerPageableAndFiltered(
                filteredAndPageableRequestDTO.getPageRequest(),
                filteredAndPageableRequestDTO.getFilters()
        );

        return employerEntityPage.map(TrailerMapper.INSTANCE::toResponseDTO);
    }

    public void updateTrailer(UUID id, @Valid UpdateTrailerRequestDTO updateTrailerRequestDTO) {
        TrailerEntity trailerEntity = trailerService.findById(id);

        TrailerMapper.INSTANCE.updateEntity(trailerEntity, updateTrailerRequestDTO);

        trailerService.saveTrailer(trailerEntity);

        log.info("Trailer {} successfully updated", id);
    }

    public void deleteTrailer(UUID id) {
        trailerService.deleteTrailer(id);

        log.info("Trailer {} successfully deleted", id);
    }

    public List<SummaryMachineResponseDTO> getAllTrailersSummary() {
        return trailerService.findAllSummary().stream()
                .map(summary -> new SummaryMachineResponseDTO(summary.getId(), summary.getLicensePlate()))
                .toList();
    }
}
