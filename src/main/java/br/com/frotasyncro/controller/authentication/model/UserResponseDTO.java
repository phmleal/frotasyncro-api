package br.com.frotasyncro.controller.authentication.model;

import br.com.frotasyncro.domain.authentication.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private UUID id;

    private String username;

    private String email;

    private UserStatus status;

    private LocalDateTime createdAt;

    private Set<RoleResponseDTO> roles;

}
