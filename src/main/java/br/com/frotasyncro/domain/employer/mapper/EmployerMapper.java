package br.com.frotasyncro.domain.employer.mapper;

import br.com.frotasyncro.controller.employer.model.CreateEmployerRequestDTO;
import br.com.frotasyncro.controller.employer.model.EmployerResponseDTO;
import br.com.frotasyncro.controller.employer.model.UpdateEmployerRequestDTO;
import br.com.frotasyncro.domain.authentication.enums.UserStatus;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import br.com.frotasyncro.infrastructure.persistence.employer.entities.EmployerEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {UserStatus.class})
public interface EmployerMapper {

    EmployerMapper INSTANCE = Mappers.getMapper(EmployerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "user")
    EmployerEntity toEntity(CreateEmployerRequestDTO createUserRequestDTO, UserEntity user);

    @Mapping(target = "active", expression = "java(employerEntity.getUser().getStatus().isActive())")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "admin", expression = "java(employerEntity.getUser()" +
            ".isAdmin())")
    EmployerResponseDTO toResponseDTO(EmployerEntity employerEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "socialNumber", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget EmployerEntity employerEntity, UpdateEmployerRequestDTO requestDTO);

}
