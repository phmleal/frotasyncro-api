package br.com.frotasyncro.domain.machine.wheeling.mapper;

import br.com.frotasyncro.controller.machine.model.CreateTruckRequestDTO;
import br.com.frotasyncro.controller.machine.model.TruckResponseDTO;
import br.com.frotasyncro.controller.machine.model.UpdateTruckRequestDTO;
import br.com.frotasyncro.domain.machine.enums.MachineStatus;
import br.com.frotasyncro.domain.machine.enums.MachineType;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {MachineType.class, MachineStatus.class})
public interface TruckMapper {

    TruckMapper INSTANCE = Mappers.getMapper(TruckMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "machineType", expression = "java(MachineType.TRUCK)")
    @Mapping(target = "machineStatus", expression = "java(MachineStatus.ACTIVE)")
    TruckEntity toEntity(CreateTruckRequestDTO createTruckRequestDTO);

    TruckResponseDTO toResponseDTO(TruckEntity truck);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget TruckEntity truckEntity, UpdateTruckRequestDTO requestDTO);

}
