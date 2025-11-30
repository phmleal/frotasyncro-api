package br.com.frotasyncro.domain.machine.wheeling.mapper;

import br.com.frotasyncro.controller.machine.model.CreateTrailerRequestDTO;
import br.com.frotasyncro.controller.machine.model.TrailerResponseDTO;
import br.com.frotasyncro.controller.machine.model.UpdateTrailerRequestDTO;
import br.com.frotasyncro.domain.machine.enums.MachineStatus;
import br.com.frotasyncro.domain.machine.enums.MachineType;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TrailerEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {MachineType.class, MachineStatus.class})
public interface TrailerMapper {

    TrailerMapper INSTANCE = Mappers.getMapper(TrailerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "machineType", expression = "java(MachineType.TRAILER)")
    @Mapping(target = "machineStatus", expression = "java(MachineStatus.ACTIVE)")
    TrailerEntity toEntity(CreateTrailerRequestDTO createTrailerRequestDTO);

    TrailerResponseDTO toResponseDTO(TrailerEntity trailer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget TrailerEntity trailerEntity, UpdateTrailerRequestDTO requestDTO);

}
