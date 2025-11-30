package br.com.frotasyncro.controller.authentication;

import br.com.frotasyncro.application.authentication.AuthenticationApplicationService;
import br.com.frotasyncro.controller.authentication.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationApplicationService authenticationApplicationService;

    @PostMapping
    @Operation(summary = "Get authentication by username and password")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody @Valid
                                                                  AuthenticationRequestDTO authenticationRequestDTO) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationApplicationService.authenticate(authenticationRequestDTO);

        return ResponseEntity.ok(authenticationResponseDTO);
    }

    @PostMapping("/passwords-forget/otp")
    @Operation(summary = "Forget password - Otp generation")
    public ResponseEntity<Void> forgetPassword(@RequestBody @Valid ForgetPasswordRequestDTO forgetPasswordRequestDTO) {
        authenticationApplicationService.forgetPassword(forgetPasswordRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @PostMapping("/passwords-forget/otp/validation")
    @Operation(summary = "Validate Otp")
    public ResponseEntity<ValidateOtpResponseDTO> validateOtp(@RequestBody ValidateOtpRequestDTO validateOtpRequestDTO) {
        ValidateOtpResponseDTO validateOtpResponseDTO = authenticationApplicationService.validateOtp(validateOtpRequestDTO);

        return ResponseEntity.ok(validateOtpResponseDTO);
    }

    @PatchMapping("/passwords-forget/update")
    @Operation(summary = "Update password")
    public ResponseEntity<Void> updateUserPassword(@RequestBody @Valid UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        authenticationApplicationService.updateUserPassword(updatePasswordRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }


    @PostMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create user")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid CreateUserRequestDTO createUserRequestDTO) {
        UserResponseDTO userResponseDTO = authenticationApplicationService.createUser(createUserRequestDTO);

        return new ResponseEntity<>(userResponseDTO, CREATED);
    }

    @PatchMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update user")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        authenticationApplicationService.updateUser(id, updateUserRequestDTO);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get user detail")
    public ResponseEntity<UserResponseDTO> getUserDetail(@PathVariable UUID id) {
        UserResponseDTO userResponseDTO = authenticationApplicationService.getUserDetail(id);

        return new ResponseEntity<>(userResponseDTO, OK);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "List roles")
    public ResponseEntity<List<RoleResponseDTO>> listRoles() {
        List<RoleResponseDTO> roleResponseDTOList = authenticationApplicationService.listRoles();

        return new ResponseEntity<>(roleResponseDTOList, OK);
    }
}
