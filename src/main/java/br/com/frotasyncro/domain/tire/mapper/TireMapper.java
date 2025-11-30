package br.com.frotasyncro.domain.tire.mapper;

import br.com.frotasyncro.controller.tire.model.*;
import br.com.frotasyncro.domain.tire.enums.TireCondition;
import br.com.frotasyncro.domain.tire.enums.TireSide;
import br.com.frotasyncro.domain.tire.enums.TireStatus;
import br.com.frotasyncro.domain.tire.model.TireReport;
import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TireHistoryEntity;
import br.com.frotasyncro.infrastructure.persistence.tire.entities.TirePositionEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = {TireCondition.class, TireStatus.class})
public interface TireMapper {

    TireMapper INSTANCE = Mappers.getMapper(TireMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tireCondition", expression = "java(createTireRequestDTO.getTireCondition() != null ? createTireRequestDTO.getTireCondition() : TireCondition.NEW)")
    @Mapping(target = "tireStatus", expression = "java(TireStatus.ACTIVE)")
    TireEntity toEntity(CreateTireRequestDTO createTireRequestDTO);

    @Mapping(target = "history", source = "history")
    TireResponseDTO toResponseDTO(TireEntity tireEntity);

    TireHistoryResponseDTO toTireHistoryResponseDTO(TireHistoryEntity tireHistoryEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget TireEntity tireEntity, UpdateTireRequestDTO updateTireRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "inUse", constant = "true")
    TirePositionEntity toTirePositionEntity(TireEntity tire, MachineEntity machine, int axle, TireSide side);

    @Mapping(target = "side", expression = "java(tirePositionEntity.getSide())")
    TirePositionResponseDTO toTirePositionEntityResponseDTO(TirePositionEntity tirePositionEntity);

    default TirePositionByMachineResponseDTO toTirePositionByMachineResponseDTO(MachineEntity machineEntity, List<TirePositionEntity> tirePositionEntityList) {
        return TirePositionByMachineResponseDTO.builder()
                .tiresPositions(tirePositionEntityList.stream().map(this::toTirePositionEntityResponseDTO).toList())
                .build();
    }

    @Mapping(target = "mileage", source = "mileage")
    @Mapping(target = "tire", source = "tire")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TireHistoryEntity toTireRetreadingEntity(TireEntity tire);

    TireReport toTireReport(TireResponseDTO tireResponseDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "mileage", source = "tire.mileage")
    @Mapping(target = "type", source = "createTireHistoryRequestDTO.type")
    @Mapping(target = "observation", source = "createTireHistoryRequestDTO.observation")
    @Mapping(target = "tire", source = "tire")
    TireHistoryEntity toTireHistoryEntity(TireEntity tire, CreateTireHistoryRequestDTO createTireHistoryRequestDTO);
}