package br.com.frotasyncro.application.tire;

import br.com.frotasyncro.controller.model.FilteredAndPageableRequestDTO;
import br.com.frotasyncro.controller.tire.model.*;
import br.com.frotasyncro.domain.authentication.AuthenticationService;
import br.com.frotasyncro.domain.employer.EmployerService;
import br.com.frotasyncro.domain.machine.MachineService;
import br.com.frotasyncro.domain.machine.wheeling.TruckTrailerCombinationService;
import br.com.frotasyncro.domain.report.ReportService;
import br.com.frotasyncro.domain.tire.TireService;
import br.com.frotasyncro.domain.tire.enums.TireCondition;
import br.com.frotasyncro.domain.tire.enums.TireEventType;
import br.com.frotasyncro.domain.tire.enums.TireSide;
import br.com.frotasyncro.domain.tire.exception.AlreadyExistsTirePositionException;
import br.com.frotasyncro.domain.tire.mapper.TireMapper;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireHistoryEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TirePositionEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class TireApplicationService {

    private final TireService tireService;
    private final MachineService machineService;
    private final ReportService reportService;

    private final AuthenticationService authenticationService;
    private final EmployerService employerService;
    private final TruckTrailerCombinationService truckTrailerCombinationService;

    public Page<SummaryTireResponseDTO> getSummaryTires(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        UserEntity userEntity = authenticationService.getLoggedUser();

        if (!userEntity.isAdmin()) {
            if (isNull(filteredAndPageableRequestDTO.getFilters())) {
                filteredAndPageableRequestDTO.setFilters(new ArrayList<>());
            }

            EmployerEntity employer = employerService.findByUser(userEntity);

            return tireService.listTiresByEmployerPageable(employer,
                    filteredAndPageableRequestDTO.getPageRequest());
        }

        return tireService.listSummaryTirePageableAndFiltered(
                filteredAndPageableRequestDTO.getPageRequest()
        );
    }

    public Page<SummaryTireResponseDTO> getAvailableTiresForNewPositions(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        return tireService.listAvailableTiresForNewPositions(
                filteredAndPageableRequestDTO.getPageRequest()
        );
    }

    public TireResponseDTO getTireDetail(UUID id) {
        TireEntity tireEntity = tireService.findById(id);

        TireResponseDTO tireResponseDTO = TireMapper.INSTANCE.toResponseDTO(tireEntity);

        Optional<TirePositionEntity> tirePositionEntityOptional = tireService.findTirePositionByTire(tireEntity);

        tirePositionEntityOptional.ifPresent(tirePositionEntity -> {
            tireResponseDTO.setLicensePlate(tirePositionEntity.getMachine().getLicensePlate());
            tireResponseDTO.setMachineId(tirePositionEntity.getMachine().getId());
            tireResponseDTO.setEquipmentPosition(String.format("%d-%s", tirePositionEntity.getAxle(), tirePositionEntity.getSide()));
        });

        return tireResponseDTO;
    }

    public TireResponseDTO createTire(CreateTireRequestDTO createTireRequestDTO) {
        TireEntity tireEntity = TireMapper.INSTANCE.toEntity(createTireRequestDTO);

        tireService.saveTire(tireEntity);

        log.info("Tire {} successfully created", tireEntity.getId());

        return TireMapper.INSTANCE.toResponseDTO(tireEntity);
    }

    public Page<TireResponseDTO> getTires(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        Page<TireEntity> tireEntityPage =
                tireService.listTirePageableAndFiltered(
                        filteredAndPageableRequestDTO.getPageRequest(),
                        filteredAndPageableRequestDTO.getFilters()
                );

        return tireEntityPage.map(TireMapper.INSTANCE::toResponseDTO);
    }

    public void updateTire(UUID id, UpdateTireRequestDTO updateTireRequestDTO) {
        TireEntity tireEntity = tireService.findById(id);

        TireMapper.INSTANCE.updateEntity(tireEntity, updateTireRequestDTO);

        tireService.saveTire(tireEntity);

        log.info("Tire {} successfully updated", id);
    }

    public void deleteTire(UUID id) {
        tireService.deleteTire(id);

        log.info("Tire {} successfully deleted", id);
    }

    @Transactional
    public TirePositionResponseDTO addTirePositionToMachine(CreateTirePositionRequestDTO createTirePositionRequestDTO) {
        checkIfExistsTirePositionInUse(
                createTirePositionRequestDTO.getMachineId(),
                createTirePositionRequestDTO.getTireId(),
                createTirePositionRequestDTO.getAxle(),
                createTirePositionRequestDTO.getSide()
        );

        TireEntity tireEntity = tireService.findById(createTirePositionRequestDTO.getTireId());

        MachineEntity machineEntity = machineService.findById(createTirePositionRequestDTO.getMachineId());

        TirePositionEntity tirePositionEntity = TireMapper.INSTANCE.toTirePositionEntity(
                tireEntity, machineEntity,
                createTirePositionRequestDTO.getAxle(),
                createTirePositionRequestDTO.getSide()
        );

        tireService.saveTirePosition(tirePositionEntity);

        buildAndSaveTireEvent(tireEntity, Optional.ofNullable(tirePositionEntity),
                new CreateTireHistoryRequestDTO(TireEventType.INSTALLATION));

        log.info("Tire Position {} successfully created", tirePositionEntity.getId());

        return TireMapper.INSTANCE.toTirePositionEntityResponseDTO(tirePositionEntity);
    }

    private void buildAndSaveTireEvent(TireEntity tireEntity,
                                       Optional<TirePositionEntity> tirePositionEntity,
                                       CreateTireHistoryRequestDTO createTireHistoryRequestDTO) {

        TireHistoryEntity tireHistoryEntity = buildTireHistoryWithDetails(
                createTireHistoryRequestDTO,
                tireEntity,
                tirePositionEntity
        );

        tireService.saveTireHistory(tireHistoryEntity);
    }

    private void checkIfExistsTirePositionInUse(UUID machineId, UUID tireId,
                                                int axle, TireSide side) {
        var alreadyExistsTirePositionWithSameParameters = tireService.existsTirePositionInUse(
                machineId,
                tireId,
                axle,
                side
        );

        if (TRUE.equals(alreadyExistsTirePositionWithSameParameters)) {
            var errorMessage = String.format("Tire position already exists in" +
                            " use for machineId: %s, tireId: %s, axle: %d, side: %s",
                    machineId,
                    tireId,
                    axle,
                    side);

            log.error(errorMessage);

            throw new AlreadyExistsTirePositionException(errorMessage);
        }
    }

    public TirePositionByMachineResponseDTO getTiresPositionsInUseByMachine(UUID machineId) {
        MachineEntity machineEntity = machineService.findById(machineId);

        List<TirePositionEntity> tirePositionEntityList = tireService.findTiresPositionsInUseByMachine(machineEntity);

        return TireMapper.INSTANCE.toTirePositionByMachineResponseDTO(machineEntity, tirePositionEntityList);
    }

    @Transactional
    public void inactivateTirePosition(UUID tirePositionId, InactivateTirePositionRequestDTO inactivateTirePositionRequestDTO) {
        TirePositionEntity tirePositionEntity = tireService.findTirePositionById(tirePositionId);

        TireEntity tireEntity = tirePositionEntity.getTire();

        buildAndSaveTireEvent(tireEntity, Optional.of(tirePositionEntity),
                new CreateTireHistoryRequestDTO(inactivateTirePositionRequestDTO.getEventType()));

        tireService.inactivateTirePosition(tirePositionEntity);

        log.info("Tire Position {} successfully inactivated", tirePositionId);
    }

    @SneakyThrows
    public byte[] generateTiresReport(FilteredAndPageableRequestDTO filteredAndPageableRequestDTO) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            reportService.generateExcel(
                    out,
                    (page, size) -> {
                        filteredAndPageableRequestDTO.initPagination(page, size);

                        return getTires(filteredAndPageableRequestDTO)
                                .map(TireMapper.INSTANCE::toTireReport)
                                .stream().toList();
                    },
                    10000000
            );

            return out.toByteArray();
        } catch (Exception e) {
            throw new IOException("Erro ao gerar relatório", e);
        }
    }

    @Transactional
    public void addEventToTireHistory(UUID tireId,
                                      CreateTireHistoryRequestDTO createTireHistoryRequestDTO) {
        TireEntity tireEntity = tireService.findById(tireId);

        Optional<TirePositionEntity> tirePositionEntityOptional =
                tireService.findTirePositionByTire(tireEntity);

        buildAndSaveTireEvent(tireEntity, tirePositionEntityOptional, createTireHistoryRequestDTO);

        log.info("Event {} successfully added to tire {}",
                createTireHistoryRequestDTO.getType(), tireId);
    }

    private TireHistoryEntity buildTireHistoryWithDetails(CreateTireHistoryRequestDTO createTireHistoryRequestDTO,
                                                          TireEntity tireEntity,
                                                          Optional<TirePositionEntity> tirePositionEntityOptional) {

        TireHistoryEntity tireHistoryEntity = TireMapper.INSTANCE.toTireHistoryEntity(tireEntity, createTireHistoryRequestDTO);

        tirePositionEntityOptional.ifPresent(tirePositionEntity -> {
            tireHistoryEntity.setPosition(String.format("%d-%s", tirePositionEntity.getAxle(), tirePositionEntity.getSide()));
            tireHistoryEntity.setLicensePlate(tirePositionEntity.getMachine().getLicensePlate());
        });

        if (createTireHistoryRequestDTO.getType().equals(TireEventType.RECAP)) {
            switch (tireEntity.getTireCondition()) {
                case NEW -> tireEntity.setTireCondition(TireCondition.RECAPPED);
                case RECAPPED ->
                        tireEntity.setTireCondition(TireCondition.TWO_TIMES_RECAPPED);
                case TWO_TIMES_RECAPPED, SCRAPPED ->
                        tireEntity.setTireCondition(TireCondition.SCRAPPED);
            }

            tireService.saveTire(tireEntity);
        }

        return tireHistoryEntity;
    }
}
