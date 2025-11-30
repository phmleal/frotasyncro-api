package br.com.frotasyncro.domain.machine.wheeling.mapper;

import br.com.frotasyncro.controller.machine.model.SummaryMachineResponseDTO;
import br.com.frotasyncro.controller.machine.model.TruckTrailerCombinationResponseDTO;
import br.com.frotasyncro.controller.machine.model.UpdateTruckTrailerCombinationRequestDTO;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TrailerEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckEntity;
import br.com.frotasyncro.infrastructure.persistence.machine.wheeling.entities.TruckTrailerCombinationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper
public interface TruckTrailerCombinationMapper {

    TruckTrailerCombinationMapper INSTANCE = Mappers.getMapper(TruckTrailerCombinationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "finalMileage", ignore = true)
    @Mapping(target = "finishedAt", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    TruckTrailerCombinationEntity toEntity(TruckEntity truck,
                                           TrailerEntity trailer,
                                           EmployerEntity employer,
                                           Long initialMileage,
                                           LocalDateTime createdAt);

    TruckTrailerCombinationResponseDTO toResponseDTO(TruckTrailerCombinationEntity truckTrailerCombinationEntity);

    SummaryMachineResponseDTO toSummaryResponseDTO(TruckEntity truck);

    SummaryMachineResponseDTO toSummaryResponseDTO(TrailerEntity trailer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "truck", ignore = true)
    @Mapping(target = "trailer", ignore = true)
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget TruckTrailerCombinationEntity entity, UpdateTruckTrailerCombinationRequestDTO requestDTO);

}
