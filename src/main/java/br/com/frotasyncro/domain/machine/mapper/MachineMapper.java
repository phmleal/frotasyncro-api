package br.com.frotasyncro.domain.machine.mapper;

import br.com.frotasyncro.controller.machine.model.MachineResponseDTO;
import br.com.frotasyncro.domain.machine.enums.MachineType;
import br.com.frotasyncro.infrastructure.persistence.machine.entities.MachineEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {MachineType.class})
public interface MachineMapper {

    MachineMapper INSTANCE = Mappers.getMapper(MachineMapper.class);

    MachineResponseDTO toResponseDTO(MachineEntity machine);

}
