package br.com.frotasyncro.domain.authentication.mapper;

import br.com.frotasyncro.domain.authentication.enums.OtpStatus;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.OtpEntity;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(imports = {LocalDateTime.class, OtpStatus.class})
public interface OtpMapper {

    OtpMapper INSTANCE = Mappers.getMapper(OtpMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "attempts", constant = "0")
    @Mapping(target = "otpStatus", expression = "java(OtpStatus.PENDING)")
    @Mapping(target = "lastAttemptAt", ignore = true)
    @Mapping(target = "expiresAt", expression = "java(LocalDateTime.now().plusMinutes(30))")
    OtpEntity createInitialOtpEntity(UserEntity user, String hash, String salt);

}
