package br.com.frotasyncro.application.authentication;

import br.com.frotasyncro.controller.authentication.model.*;
import br.com.frotasyncro.domain.authentication.AuthenticationService;
import br.com.frotasyncro.domain.authentication.OtpService;
import br.com.frotasyncro.domain.authentication.RoleService;
import br.com.frotasyncro.domain.authentication.UserService;
import br.com.frotasyncro.domain.authentication.mapper.AuthenticationMapper;
import br.com.frotasyncro.domain.authentication.mapper.RoleMapper;
import br.com.frotasyncro.domain.authentication.mapper.UserMapper;
import br.com.frotasyncro.domain.authentication.model.Authentication;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.RoleEntity;
import br.com.frotasyncro.infrastructure.persistence.authentication.entities.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationApplicationService {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final RoleService roleService;
    private final OtpService otpService;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        Authentication authentication = authenticationService.authenticateByUsernameAndPassword(
                authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword()
        );

        return AuthenticationMapper.INSTANCE.toAuthenticationResponseDTO(authentication);
    }

    public UserResponseDTO createUser(@Valid CreateUserRequestDTO createUserRequestDTO) {
        UserEntity user = createUserEntity(createUserRequestDTO, false);

        log.info("User {} has successfully created", user.getId());

        return UserMapper.INSTANCE.toUserResponseDTO(user);
    }

    public UserEntity createUserEntity(CreateUserRequestDTO createUserRequestDTO, boolean admin) {
        Set<RoleEntity> basicRoles =
                new HashSet<>(admin ? roleService.findAdminRoles() :
                        roleService.findBasicRoles());

        UserEntity user = UserMapper.INSTANCE.toUserEntity(createUserRequestDTO, basicRoles);

        userService.createUserWithoutEmail(user);

        return user;
    }

    public void updateUser(UUID id, @Valid UpdateUserRequestDTO updateUserRequestDTO) {
        UserEntity user = userService.getUserEntityById(id);

        UserMapper.INSTANCE.updateUserEntity(user, updateUserRequestDTO);

        userService.updateUser(user);

        log.info("User {} has successfully updated", id);
    }

    public UserResponseDTO getUserDetail(UUID id) {
        UserEntity user = userService.getUserEntityById(id);

        return UserMapper.INSTANCE.toUserResponseDTO(user);
    }

    public List<RoleResponseDTO> listRoles() {
        log.info("Fetching all roles");

        return roleService
                .findAllRoles()
                .stream()
                .map(RoleMapper.INSTANCE::toRoleResponseDTO)
                .toList();
    }

    @Transactional
    public void forgetPassword(ForgetPasswordRequestDTO forgetPasswordRequestDTO) {
        UserEntity user = userService.getUserEntityByUsername(forgetPasswordRequestDTO.getUsername());

        otpService.createOtp(user);

        userService.putUserOnConfigurationSituation(user);

        log.info("OTP has been successfully created for user id {}", user.getId());
    }

    public ValidateOtpResponseDTO validateOtp(ValidateOtpRequestDTO validateOtpRequestDTO) {
        UserEntity user = userService.getUserEntityByUsername(validateOtpRequestDTO.getUsername());

        Boolean isValid = otpService.verifyOtpCode(user, validateOtpRequestDTO.getOtp());

        return new ValidateOtpResponseDTO(isValid);
    }

    public void updateUserPassword(@Valid UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        userService.updatePassword(updatePasswordRequestDTO.getUsername(), updatePasswordRequestDTO.getPassword());

        log.info("Username {} has password successfully updated", updatePasswordRequestDTO.getUsername());
    }
}
