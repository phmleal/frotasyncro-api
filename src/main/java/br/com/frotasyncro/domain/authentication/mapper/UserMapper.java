package br.com.frotasyncro.domain.authentication.mapper;

import br.com.frotasyncro.controller.authentication.model.CreateUserRequestDTO;
import br.com.frotasyncro.controller.authentication.model.UpdateUserRequestDTO;
import br.com.frotasyncro.controller.authentication.model.UserResponseDTO;
import br.com.frotasyncro.domain.authentication.enums.UserStatus;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.RoleEntity;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(imports = {UserStatus.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    UserEntity toUserEntity(CreateUserRequestDTO createUserRequestDTO, Set<RoleEntity> roles);

    UserResponseDTO toUserResponseDTO(UserEntity user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserEntity(@MappingTarget UserEntity user, UpdateUserRequestDTO updateUserRequestDTO);

}
