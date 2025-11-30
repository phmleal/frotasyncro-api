package br.com.frotasyncro.controller.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponseDTO {

    private UUID id;
    private String authority;
    private String description;

}
