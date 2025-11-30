package br.com.frotasyncro.domain.authentication.mapper;

import br.com.frotasyncro.controller.authentication.model.RoleResponseDTO;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleResponseDTO toRoleResponseDTO(RoleEntity roleEntity);

}
