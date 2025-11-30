package br.com.frotasyncro.controller.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {

    private String sub;

    private String accessToken;

    private long expiresIn;

    private Set<RoleResponseDTO> authorities;
    
}

